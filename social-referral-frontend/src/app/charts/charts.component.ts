import { Network, DataSet, Node, Edge, IdType } from 'vis';
import { Component , OnInit, OnDestroy } from '@angular/core';

import * as Chartist from 'chartist';

@Component({
    selector: 'app-charts-cmp',
    templateUrl: './charts.component.html'
})

export class ChartsComponent implements OnInit {
    
  public nodes: Node;
  public edges: Edge;
  public network : Network;

  public ngOnInit(): void {
        var nodes = new DataSet([
            {id: 1, label: 'Aditya'},
            {id: 2, label: 'Praveen'},
            {id: 3, label: 'Nikhil'},
            {id: 4, label: 'Manish'},
            {id: 5, label: 'Pranav'},
        {id:6,label:'Kirti'},
        {id:7,label:'Shalini'}
        ]);
          // create an array with edges
          var edges = new DataSet([
            {from: 1, to: 3},
            {from: 1, to: 2},
            {from: 2, to: 4},
            {from: 2, to: 5},
            {from: 2, to: 6},
            {from: 3, to: 7}
            

          ]);
         // create a network
        var container = document.getElementById('mynetwork');
        var data = {
          nodes: nodes,
          edges: edges
        };
        //var options = {};

        var options = {
          autoResize: true,
          height: '100%',
          width: '100%',
          locale: 'en',
          clickToUse: true,
          nodes:{},
          edges:{
            arrows: 'to',
            color: 'red',
            font: '12px arial #ff0000',
            scaling:{
              label: true,
            },
            shadow: true,
            smooth: true,
          }
             // defined in the configure module.
         /* edges: {...},        // defined in the edges module.
          nodes: {...},        // defined in the nodes module.
          groups: {...},       // defined in the groups module.
          layout: {...},       // defined in the layout module.
          interaction: {...},  // defined in the interaction module.
          manipulation: {...}, // defined in the manipulation module.
          physics: {...},      // defined in the physics module. */
        }
      //  
     
     
      
        var network = new Network(container, data,options);
        //network.setOptions(options);
  }
}
