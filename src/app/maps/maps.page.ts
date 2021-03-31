import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Map, tileLayer, marker } from 'leaflet';


@Component({
  selector: 'app-maps',
  templateUrl: './maps.page.html',
  styleUrls: ['./maps.page.scss'],
})
export class MapsPage implements OnInit {

  map: Map;
  newMarker: any;
  address: string[];
  constructor(private router: Router) { }

  ngOnInit(): void {

  }


  ionViewDidEnter() {
    this.loadMap();
  }

  loadMap() {
    this.map = new Map('mapId').setView([45, -8], 13);
    tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: 'AVA' }).addTo(this.map);
  }


}
