Feature: Booking Seat Hold
  As a passenger
  I want to hold a seat on a trip
  So that I can proceed to payment before it expires

  Scenario: Place a seat hold successfully
    Given I am connected as user id "user-1" with the roles "PASSENGER"
    And I assume that the trips with the following data are inside the database
      | id                                   | agencyId                             | routeId                              | busId                                | priceXaf | travelClass | status | seatsTotal | seatsSold |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | 99999999-9999-9999-9999-999999999999 | aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa | cccccccc-cccc-cccc-cccc-cccccccccccc | 5000     | STANDARD    | OPEN   | 40         | 0         |
    When I place a booking hold with the following data
      | tripId                               | seatNos | passengerName | passengerMsisdn |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | 7       | Jean Mbarga   | +237699000111   |
    And the hold response contains a booking reference for the following data
      | seatNos |
      | 7       |

  Scenario: Reject a second hold on the same seat
    Given I am connected as user id "user-1" with the roles "PASSENGER"
    And I assume that the trips with the following data are inside the database
      | id                                   | agencyId                             | routeId                              | busId                                | priceXaf | travelClass | status | seatsTotal | seatsSold |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | 99999999-9999-9999-9999-999999999999 | aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa | cccccccc-cccc-cccc-cccc-cccccccccccc | 5000     | STANDARD    | OPEN   | 40         | 0         |
    When I place a booking hold with the following data
      | tripId                               | seatNos | passengerName | passengerMsisdn |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | 8       | Jean Mbarga   | +237699000111   |
    And the hold response contains a booking reference for the following data
      | seatNos |
      | 8       |
    When I try to place a booking hold with the following data
      | tripId                               | seatNos | passengerName   | passengerMsisdn |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | 8       | Other Passenger | +237699000222   |
    Then the last request failed with the http status "CONFLICT" and error code "BOOKING_409_001"
