package shared

import shared.geometry.Coordinates

package object car {

  case class CarsUpdate(cars: List[Car], stats: Option[Double])

  case class Car(location: Coordinates, hexColor: String)

}
