#!/usr/bin/perl -w
#

use strict;
use DBI;
use Time::Local ;

use lib "$ENV{OPSEXEC}/perl_mods";
use Connect;
use TT;

my ($evid) = @ARGV;

my $dbh = Connect::psql_logon;

my $sql;
my $csr1;
my $csr2;

$sql = "SELECT h.day, h.msec, COALESCE(COALESCE(h.depth,h.depdp),r.default_depth),
               ( SELECT MAX(a.delta) FROM association a WHERE a.hypid = h.hypid AND a.author = 'ISC' )
          FROM hypocenter h, event e, region r
         WHERE h.isc_evid = $evid
           AND h.isc_evid = e.evid
           AND h.hypid = e.prime_hyp
           AND grn_ll(h.lat,h.lon) = r.gr_number";

$csr1 = $dbh->prepare($sql);
$csr1->execute() or die $dbh->errstr;
my ( $day, $msec, $depth, $maxdelta ) = $csr1->fetchrow_array;
$csr1->finish;

$maxdelta = int($maxdelta) + 1;

$dbh->Connect::psql_logoff or warn $dbh->errstr;

#0123456789012345678
#YYYY-MM-DD HH:MI:SS
my $yy = substr $day,  0, 4;
my $mm = substr $day,  5, 2;
my $dd = substr $day,  8, 2;
my $hh = substr $day, 11, 2;
my $mi = substr $day, 14, 2;
my $ss = substr $day, 17, 2;

$msec /= 1000.0;

#
##
#

my %phases;

my $conf = sprintf "/export/isc-linux/ops/etc/iscloc/ak135_model.txt";

open( IN, "<$conf" ) or die;

until ( eof(IN) ) {

    my $line = <IN>;
    chomp $line;

    if ( $line =~ /^allowable_phases/ ) {

        until ( eof(IN) ) {

            $line = <IN>;
            chomp $line;
            $line =~ s/ //g;

            last if ( $line =~ /^$/ );

            $phases{"$line"} = 0;

        }

    }

}

close IN;

#
##
#

my %tt = ();

my @phases = read_tt_tables("ak135", "/export/isc-linux/ops/etc/", \%tt);

foreach my $phase ( @phases ) {

    next if ( ! defined $phases{$phase} );

    my $int = 1;

    for ( my $delta = 0.05; $delta < $maxdelta; $delta += $int ) {

        my ($ttime, $dtdd, $dtdh, $bpdel) = table_tt($tt{$phase}, $delta, $depth, 1, 1);

        next if ( $ttime < -998 );

        my $ttmsec = ($ttime - int($ttime)) + $msec;

        $ttime = int($ttime);

        if ( $ttmsec > 1 ) {

            $ttime += 1;
            $ttmsec -= 1;

        }

        my $datetime = correct_time($yy, $mm, $dd, $hh, $mi, $ss + $ttime) ;

        printf "%s,%.4f,%s.%03d,\n", $phase, $delta, $datetime, ($ttmsec * 1000);

=head1
        if ( $delta < 5 ) {

            $int = 0.1;

        } elsif (  $delta < 10 ) {

            $int = 0.2;

        } elsif (  $delta < 20 ) {

            $int = 0.5;

        } else {

            $int = 1.0;

        }
=cut

        if ( $delta < 5 ) {

            $int = 0.4;

        } elsif (  $delta < 10 ) {

            $int = 0.8;

        } elsif (  $delta < 20 ) {

            $int = 1.0;

        } else {

            $int = 2.0;

        }


    }

}

#
##
#

exit;

#
##
#

sub days_per_month {

    my ($mm,$yyyy) = @_;

    my @days_per_month = ( 0,31,28,31,30,31,30,31,31,30,31,30,31);

    # Case of typical year or not February
    return $days_per_month[$mm] if ($mm !=2 or $yyyy%4 != 0);

    # Case of February in a typical leap year
    return 29 if ($yyyy%100 != 0);

    # Case of February in last year of a typical century
    return 28 if ($yyyy%400 != 0);

    # Case of February in last year of a special century
    return 29;
}

#
##
#

sub correct_time{

    my ($yyyy,$mm,$dd,$hh,$mi,$ss) = @_;

    my ($month,$days_in_month);

    while ($ss > 59) {

        $mi++;
        $ss -= 60;

    }

    while ( $mi > 59 ) {

        $hh++;
        $mi -= 60;

    }

    while ( $hh > 24 ){

        $dd++;
        $hh-=24;

    }

    $days_in_month = days_per_month($mm,$yyyy);

    if ($dd>$days_in_month){

        $mm++;
        $dd-=$days_in_month;

    }

    if ( $mm == 13 ) {

        $mm++;
        $yyyy++;

    }

    return sprintf "%4d-%02d-%02d %02d:%02d:%02d", $yyyy,$mm,$dd,$hh,$mi,$ss;

}

#
##  EOF
#
