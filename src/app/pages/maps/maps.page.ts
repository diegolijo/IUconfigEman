import { Component, OnInit } from '@angular/core';
import { LaunchNavigator, LaunchNavigatorOptions } from '@ionic-native/launch-navigator/ngx';
import { Platform } from '@ionic/angular';
import * as L from 'leaflet';
import { Map, tileLayer } from 'leaflet';
import 'leaflet-routing-machine';
import { Helper } from '../../services/Helper';
import { NativePlugin } from '../../services/NativePlugin';




@Component({
  selector: 'app-maps',
  templateUrl: './maps.page.html',
  styleUrls: ['./maps.page.scss'],
})
export class MapsPage implements OnInit {

  L: any;
  routingControl: any;
  map: Map;

  address: string[];
  location: [number, number];

  markers: L.Marker<any>[] = [];
  indexMarker = 0;

  navToggle = true;
  lat: number;
  lng: number;
  transportMode = 'd';
  transportModes = ['d', 'w', 't'];


  constructor(
    private launchNavigator: LaunchNavigator,
    private platform: Platform,
    public helper: Helper,
    private nativePlugin: NativePlugin
  ) { }

  ngOnInit(): void {
    this.L = L;
  }


  public async ionViewDidEnter() {
    await this.loadMap();
    this.goTo(this.map);
  }

  public async loadMap() {
    this.map = new Map('mapId').setView([42.8831365, -8.5348888], 13);
    tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: 'AVA' })
      .addTo(this.map);
  }



  public async goTo(map: Map): Promise<Map> {
    return new Promise(async (resolve, reject) => {
      try {
        this.map.flyTo([42.8831365, -8.5348888]);
        resolve(map);
      } catch (err) {
        reject(err);
      }

    });
  }






  public async onClickMap() {
    try {
      const center = this.map.getCenter();
      const so = this.map.getBounds().getSouthWest();
      const ne = this.map.getBounds().getNorthEast();
      const no = this.map.getBounds().getNorthWest();
      const se = this.map.getBounds().getSouthEast();
      this.lng = center.lng;
      this.lat = center.lat;
      const marker = this.L.marker(center).addTo(this.map)
        .bindPopup('latitud:    ' + this.lat + '<br>longitud: ' + this.lng + '', { closeButton: true })
        .openPopup();

      this.markers.push(marker);
      this.indexMarker = this.markers.length - 1;
    } catch (err) {
      this.helper.showException(err);
    }
  }



  public async onClickBack() {
    try {
      this.indexMarker = (this.indexMarker === 0) ? this.markers.length - 1 : this.indexMarker - 1;
      this.lng = this.markers[this.indexMarker].getLatLng().lng;
      this.lat = this.markers[this.indexMarker].getLatLng().lat;
      this.map.flyTo(this.markers[this.indexMarker].getLatLng());
    } catch (err) {
      this.helper.showException(err);
    }
  }

  public async onClickNext() {
    try {
      this.indexMarker = (this.indexMarker === this.markers.length - 1) ? 0 : this.indexMarker + 1;
      this.lat = this.markers[this.indexMarker].getLatLng().lat;
      this.lng = this.markers[this.indexMarker].getLatLng().lng;
      this.map.flyTo(this.markers[this.indexMarker].getLatLng());
    } catch (err) {
      this.helper.showException(err);
    }
  }

  public async onClickDeleteMarkers() {
    try {
      if (this.markers.length > 0) {
        for (const marker of this.markers) {
          this.map.removeLayer(marker);
        }
        this.markers = [];
      } else if (this.routingControl) {
        this.map.removeControl(this.routingControl);
      }
    } catch (err) {
      this.helper.showException(err);
    }
  }



  public async onClickGenetareRoute() {
    /* leaflet-routing-machine.js: 17932 Está utilizando el servidor de demostración de OSRM.
       Tenga en cuenta que ** NO ES APTO PARA EL USO DE PRODUCCIÓN **.
       Consulte la política de uso del servidor de demostración: https://github.com/Project-OSRM/osrm-backend/wiki/Api-usage-policy

       Para cambiar, configure la opción serviceUrl.

       No informe problemas con este servidor ni a Leaflet Routing Machine ni a OSRM; es para
       demo solamente y, a veces, no estará disponible o funcionará de forma inesperada.

       Configure su propio servidor OSRM o utilice un proveedor de servicios de pago para la producción.*/
    try {
      const wayPoints = [];
      if (this.markers.length > 0) {
        for (const marker of this.markers) {
          wayPoints.push(marker.getLatLng());
        }
      }

      this.routingControl = this.L.Routing.control({
        waypoints: wayPoints,
        routeWhileDragging: true,
        show: false
      }).addTo(this.map);
      this.routingControl._container.style.display = 'none';
      this.onClickDeleteMarkers();
    }
    catch (err) {
      this.helper.showException(err);
    }
  }





  public async onClickLocation() {
    try {
      // Movil
      if (this.platform.is('cordova')) {
        const result = await this.nativePlugin.getLocation();
        if (result) {
          this.location = result.location;
          this.map.flyTo([result.location.lat, result.location.lon], 18);
          this.markers.push(this.L.marker(this.location).addTo(this.map)
            .bindPopup('latitud:    ' + result.location.lat + '<br>longitud: ' + result.location.lon + '', { closeButton: false })
            .openPopup());
        }
      }
      // PC
      if (!this.platform.is('cordova')) {
      }
    } catch (err) {
      this.helper.showException(err);
    }
  }


  public async onClickNavigator() {
    try {

      // Movil
      if (this.platform.is('cordova')) {
        // llamada con capacitor launchNavigator
        if (this.navToggle === true) {
          /*      { "app": "google_maps", "dType": "pos", "dest": "[42,-8]", "destNickname": "null", "sType": "none",
                         "start": "null", "startNickname": "null", "transportMode": "d", "launchMode": "maps", "extras": "null" }*/
          const options: LaunchNavigatorOptions =
          {
            app: this.launchNavigator.APP.NAVIGATION,
            transportMode: 'walking',
            launchModeGoogleMaps: 'turn-by-turn'
          };
          this.launchNavigator.navigate([this.lat, this.lng], options)
            .then(
              success => console.log('Launched navigator'),
              error => console.log('Error launching navigator', error)
            );
          // llamada a nativo  implementacion en  NatPlugin.java
        } else {
          const result = await this.nativePlugin.setNavigator(this.lat, this.lng, this.transportMode);
        }
      }

    } catch (err) {
      this.helper.showException(err);
    }
  }


}


