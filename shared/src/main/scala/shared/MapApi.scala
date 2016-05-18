package shared

import shared.map.RoadMap

trait MapApi {
  def test(): String
  def map(): RoadMap
}