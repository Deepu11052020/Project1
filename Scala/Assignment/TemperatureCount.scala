package org.itc.com

import scala.collection.mutable
import scala.io.Source

object TemperatureCount {
  def main(args: Array[String]): Unit = {
    // Input file path
    val inputFilePath = "C:\\Users\\deepu\\Scala Project\\Deepu_Mar2024\\src\\main\\scala\\Temp.txt"

    // Read the content of the input file
    val lines = Source.fromFile(inputFilePath).getLines()

    // Create a mutable map to store sensor-wise temperature count
    var temperatureCountMap = mutable.Map[String, Int]().withDefaultValue(0)

    // Iterate through each line to count temperatures greater than 50 for every sensor
    for (line <- lines) {
      val fields = line.split(",")
      if (fields.length == 3) {
        val sensor = fields(0)
        val temperature = fields(2).toDouble
        if (temperature > 50) {
          temperatureCountMap(sensor) += 1
        }
      }
    }

    // Sort the temperature count map by sensor name
    val sortedTemperatureCountMap = temperatureCountMap.toSeq.sortBy(_._1)

    // Output the temperature count for each sensor
    sortedTemperatureCountMap.foreach { case (sensor, count) =>
      println(s"$count, $sensor")
    }
  }
}
