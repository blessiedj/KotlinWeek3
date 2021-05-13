package taxipark

import kotlin.math.floor
import kotlin.math.roundToInt

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
        this.allDrivers - this.trips.map { trip -> trip.driver }.distinct();

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
        if (minTrips == 0) this.allPassengers else this.trips.map { trip -> trip.passengers }
                .flatten()
                .groupingBy { passenger: Passenger -> passenger.name }
                .eachCount()
                .filter { entry: Map.Entry<String, Int> -> entry.value >= minTrips }
                .map { entry -> entry.key }
                .sorted().map { name -> Passenger(name) }
                .toSet()

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
        this.trips.filter { trip -> trip.driver == driver }
                .map { trip -> trip.passengers }
                .flatten()
                .groupingBy { passenger: Passenger -> passenger.name }
                .eachCount()
                .filter { entry: Map.Entry<String, Int> -> entry.value > 1 }
                .map { entry -> entry.key }
                .sorted().map { name -> Passenger(name) }
                .toSet()

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> {
    val (withDiscount, withoutDiscount) = this.trips.partition { it.discount?.isFinite() == true }
    val passengersWithDisc = withDiscount.map { trip -> trip.passengers }.flatten()
            .groupingBy { passenger: Passenger -> passenger }.eachCount()
    val passengersWithoutDisc = withoutDiscount.map { trip -> trip.passengers }.flatten()
            .groupingBy { passenger: Passenger -> passenger }.eachCount()
    val passengersOnlyWithDisc = passengersWithDisc - passengersWithoutDisc.keys
    var smartPassengers = mutableSetOf<Passenger>()
    for (x in passengersWithDisc) {
        for (y in passengersWithoutDisc) {
            if (x.key.equals(y.key) && x.value > y.value) {
                smartPassengers.add(x.key)
            }
        }
    }
    smartPassengers = (smartPassengers + passengersOnlyWithDisc.keys) as MutableSet<Passenger>
    return smartPassengers.toSet()
}

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    return if (this.trips.isEmpty()) {
        null
    } else {
        val rangesWithFrequency = this.trips
                .map { it.duration/10*10 }
                .map { IntRange(it, it+9) }
                .groupingBy { it }
                .eachCount()

        val maxValue = rangesWithFrequency.values.maxBy { it }
        rangesWithFrequency.filter { it.value == maxValue }.keys.first()
    }
}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    return if (this.trips.isEmpty()) {
        false
    } else {
        val totalIncome: Double = this.trips.sumByDouble { it.cost }
        val twentyPercentDrivers: Int = floor(this.allDrivers.size * 0.2).roundToInt()
        this.trips.groupBy(keySelector = { it.driver }, valueTransform = { it.cost })
                .mapValues { it.value.sum() }
                .values.sortedByDescending { it }
                .take(twentyPercentDrivers)
                .sum() >= totalIncome * 0.8
    }
}