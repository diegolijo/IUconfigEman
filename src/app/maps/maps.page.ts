import { Component, OnInit } from '@angular/core';
import { Platform } from '@ionic/angular';
import * as L from 'leaflet';
import { Map, tileLayer } from 'leaflet';
import 'leaflet-routing-machine';
import { Helper } from '../services/Helper';
import { NativePlugin } from '../services/NativePlugin';


@Component({
  selector: 'app-maps',
  templateUrl: './maps.page.html',
  styleUrls: ['./maps.page.scss'],
})
export class MapsPage implements OnInit {

  map: Map;
  newMarker: any;
  address: string[];
  location: [number, number];
  marker: L.Marker<any>;

  mapMarkers: any[] = null;

  constructor(
    private platform: Platform,
    public helper: Helper,
    private nativePlugin: NativePlugin
  ) { }

  ngOnInit(): void {

  }


  public async ionViewDidEnter() {
    await this.loadMap();
    this.goTo(this.map);
  }

  public async loadMap() {
    this.map = new Map('mapId').setView([45, -8], 13);
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


  public async onClickLocation() {
    try {
      // Movil
      if (this.platform.is('cordova')) {
        const result = await this.nativePlugin.getLocation();
        if (result) {
          this.location = result.location;
          this.map.flyTo([result.location.lat, result.location.lon], 18);
          this.marker = L.marker(this.location).addTo(this.map)
            .bindPopup('latitud:    ' + result.location.lat + '<br>longitud: ' + result.location.lon + '', { closeButton: false })
            .openPopup();
        }
      }
      // PC
      if (!this.platform.is('cordova')) {
        /*    await this.map.flyTo([42.8831365, -8.5348888], 18);
           this.location = [42.8831365, -8.5348888];
           this.marker = L.marker(this.location).addTo(this.map)
             .bindPopup('latitud:    ' + this.location[0] + '<br>longitud: ' + this.location[1] + '', { closeButton: false })
             .openPopup(); */
        /*        L.Routing.control({
                waypoints: [
                  L.latLng(57.74, 11.94),
                  L.latLng(57.6792, 11.949)
                ]
              }).addTo(this.map); */





        /* leaflet-routing-machine.js: 17932 Está utilizando el servidor de demostración de OSRM.
        Tenga en cuenta que ** NO ES APTO PARA EL USO DE PRODUCCIÓN **.
        Consulte la política de uso del servidor de demostración: https://github.com/Project-OSRM/osrm-backend/wiki/Api-usage-policy

        Para cambiar, configure la opción serviceUrl.

        No informe problemas con este servidor ni a Leaflet Routing Machine ni a OSRM; es para
        demo solamente y, a veces, no estará disponible o funcionará de forma inesperada.

        Configure su propio servidor OSRM o utilice un proveedor de servicios de pago para la producción. */


        /*         const routingControl = L.Routing.control({
                  waypoints: [
                    L.latLng(41, -8.1),
                    L.latLng(42.8831365, -8.5348888),
                    L.latLng(41, -8.53)
                  ],
                  routeWhileDragging: true,
                  show: false
                }).addTo(this.map);*/



        // router: new L.Routing.OSRMv1({
        //   serviceUrl: ROUTER_SERVICE_URL
        // }),
        /*    plan: new L.Routing.plan([], {
             addWaypoints: false,
             draggableWaypoints: false,
             createMarker: () => undefined
           }),
             lineOptions: {
             addWaypoints: false
           },
           collapsible: true,
             show: false
         });
       } */

      }

    } catch (err) {
      this.helper.showException(err);
    }
  }



}


