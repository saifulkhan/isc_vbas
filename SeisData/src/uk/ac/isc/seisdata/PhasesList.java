/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.seisdata;

import java.util.ArrayList;

/**
 * A wrapper of the Phase List
 *
 *  
 */
public class PhasesList extends AbstractSeisData {

    private final ArrayList<Phase> phases;

    public PhasesList() {
        phases = new ArrayList<Phase>();
    }

    public ArrayList<Phase> getPhases() {
        return this.phases;
    }

}
