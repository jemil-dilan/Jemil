@scheduler @booking-expiry
Feature: Booking expiry scheduler (simulated tick)
  As the platform
  I want expired seat holds to be released by the expiry job
  So that seats become bookable again after the hold TTL

  Scenario: Simulated tick releases a backdated hold
    Given I am connected as user id "user-1" with the roles "PASSENGER"
    And I assume that the trips with the following data are inside the database
      | id                                   | agencyId                             | routeId                              | busId                                | priceXaf | travelClass | status | seatsTotal | seatsSold |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | 99999999-9999-9999-9999-999999999999 | aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa | cccccccc-cccc-cccc-cccc-cccccccccccc | 5000     | STANDARD    | OPEN   | 40         | 0         |
    When I place a booking hold with the following data
      | tripId                               | seatNos | passengerName | passengerMsisdn |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | 21      | Jean Mbarga   | +237699000111   |
    And the hold response contains a booking reference for the following data
      | seatNos |
      | 21      |
    And I backdate the hold expiry of booking ref "last" to the past
    When the booking expiry scheduler tick is simulated
    Then the backdated booking status is "EXPIRED"
    And seat 21 on the seeded trip is free for a new hold
    When I place a booking hold with the following data
      | tripId                               | seatNos | passengerName | passengerMsisdn |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | 21      | Amina Fouda   | +237699000333   |
    And the hold response contains a booking reference for the following data
      | seatNos |
      | 21      |
