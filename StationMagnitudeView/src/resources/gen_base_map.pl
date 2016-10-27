#!/usr/bin/perl -w

#DOC
#DOC   Program: gen_base_map
#DOC
#DOC   src dir: 
#DOC
#DOC      Desc: When the number of map levels is provided, the map tiles are generated and saved based on Mercator 
#DOC            projection 
#DOC Arguments:
#DOC            
#DOC
#DOC    Calls:
#DOC            pgcoast
#DOC            convert
#DOC

use strict;
use Getopt::Long;
use Math::Trig;
#use File::Copy

my $min_scale_level = 0;
my $max_scale_level = 0;
my $image_size = 256;
my $num_row = 1;
my $num_col = 1;

# Read command line
my $usage = "Usage: $0 -min=min_scale_level -max=max_scale_level\n";
$usage .= "min: minimum scale_level max: how many levels of tiles are used, e.g. 0 means 256x256, 1 means 512x512 etc\n";

GetOptions("min=i"=>\$min_scale_level,"max=s"=>\$max_scale_level)
or die($usage);

#print "$min_scale_level\n$max_scale_level\n";

#Build directory for the tiles, top directory name is the same as the scale level
#`mkdir "./Tiles"`;
for(my $level=$min_scale_level;$level<=$max_scale_level;$level++)
{
    #generate top level directories based on map levels
     my $top_dir_name = "./$level/";
     mkdir $top_dir_name;
     $num_row = 1 << $level;
     $num_col = 1 << $level;	
     for(my $column=0; $column<$num_col;$column++)
     {     
	#generate sub directory based on longitude columns
	my $sec_dir_name = "./$level/$column";
	mkdir $sec_dir_name;
	for(my $row=0; $row<$num_row; $row++)
        {
            my $region = calc_region($level,$column,$row);
	    
	    #fine till level 5
	    my $gmtcmd = "pscoast -JM2.178 -R$region -S255 -G240 -Di -X0 -Y0 > tmpmap.ps";
	    #my $gmtcmd = "pscoast -JM2.18 -R$region -S255 -G240 -Di -A0/0/1 -W0.02p -N1/0.02p -X0 -Y0 > tmpmap.ps";
	    #print $gmtcmd;
	    `$gmtcmd`;
	    #now save the tile into the corresponding directory
	    my $fileName = "./$level/$column/$row.png";
	    #print "$fileName\n";
	    
	    my $tt = "convert -rotate 90 -crop 256x256+0+2223 -density 300 tmpmap.ps $fileName";
	    #print "$tcmd\n"; 
	    `$tt`;
        }
     }
}

sub calc_region {

    my ( $scale, $col_idx, $row_idx ) = @_;
    my ($wes_pix, $eas_pix, $sos_pix, $nos_pix); 
    my ($reg, $wes, $eas, $sos, $nos);
    
    my $maxpix = $image_size << $scale;
    $wes_pix = $col_idx * $image_size + 1;
    $eas_pix = ($col_idx + 1)*$image_size;
    $sos_pix = ($row_idx + 1)*$image_size;
    $nos_pix = $row_idx * $image_size + 1;

    $wes = (($wes_pix-1)*360.0)/$maxpix - 180.0;
    $eas = ($eas_pix*360.0)/$maxpix - 180.0;

    $sos = -1*rad2deg(pi/2-2.0*atan(exp(-1.0*($sos_pix-($maxpix)/2)/($maxpix/(2*pi)))));
    $nos = -1*rad2deg(pi/2-2.0*atan(exp(-1.0*($nos_pix-1-($maxpix)/2)/($maxpix/(2*pi)))));
    #it is strange why right shift doesn't work
    #$sos = -1*rad2deg(pi/2-2*atan(exp(-1.0*($sos_pix-($maxpix)>>1)/($maxpix/(2*pi)))));
    #$nos = -1*rad2deg(pi/2-2*atan(exp(-1.0*($nos_pix-($maxpix)>>1)/($maxpix/(2*pi)))));

    $reg = sprintf "%4.4f/%4.4f/%4.4f/%4.4f ", $wes, $eas, $sos, $nos;
    #print $reg;
    return $reg;
}
