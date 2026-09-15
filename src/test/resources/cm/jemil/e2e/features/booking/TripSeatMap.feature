Feature: Trip seat map
  As a passenger
  I want to see which seats are free on a departure
  So that I can pick an available seat before placing a hold

  Scenario: Seat map returns bus layout from the seeded trip
    Given I am connected as user id "user-1" with the roles "PASSENGER"
    And I assume that the trips with the following data are inside the database
      | id                                   | agencyId                             | routeId                              | busId                                | priceXaf | travelClass | status | seatsTotal | seatsSold |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | 99999999-9999-9999-9999-999999999999 | aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa | cccccccc-cccc-cccc-cccc-cccccccccccc | 5000     | STANDARD    | OPEN   | 40         | 0         |
    When I fetch the seat map for trip "dddddddd-dddd-dddd-dddd-dddddddddddd"
    Then the seat map has seat count 40 and layout "2-2"

  Scenario: Held seats appear as taken on the seat map
    Given I am connected as user id "user-1" with the roles "PASSENGER"
    And I assume that the trips with the following data are inside the database
      | id                                   | agencyId                             | routeId                              | busId                                | priceXaf | travelClass | status | seatsTotal | seatsSold |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | 99999999-9999-9999-9999-999999999999 | aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa | cccccccc-cccc-cccc-cccc-cccccccccccc | 5000     | STANDARD    | OPEN   | 40         | 0         |
    When I place a booking hold with the following data
      | tripId                               | seatNos | passengerName | passengerMsisdn |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | 33      | Jean Mbarga   | +237699000111   |
    And the hold response contains a booking reference for the following data
      | seatNos |
      | 33      |
    When I fetch the seat map for trip "dddddddd-dddd-dddd-dddd-dddddddddddd"
    Then the seat map has seat count 40 and layout "2-2"
    And the seat map taken seats include
      | seatNo |
      | 33     |

  Scenario: Unknown trip returns not found
    Given I am connected as user id "user-1" with the roles "PASSENGER"
    When I try to fetch the seat map for trip "ffffffff-ffff-ffff-ffff-ffffffffffff"
    Then the last request failed with the http status "NOT_FOUND" and error code "BOOKING_404_001"
