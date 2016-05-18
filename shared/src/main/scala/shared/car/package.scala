package shared

import shared.geometry.Coordinates

package object car {

  case class CarsList(cars: List[Car])

  case class Car(location: Coordinates, hexColor: String)

}
