@booking @trip-search
Feature: Trip Search
  As a passenger
  I want to search scheduled trips by cities and date
  So that I can choose a departure to book

  Scenario: Search the seeded Douala to Yaoundé trip
    Given I am connected as user id "admin-1" with the roles "ADMIN"
    And I assume that the trips with the following data are inside the database
      | id                                   | agencyId                             | routeId                              | busId                                | priceXaf | travelClass | status | seatsTotal | seatsSold |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | 99999999-9999-9999-9999-999999999999 | aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa | cccccccc-cccc-cccc-cccc-cccccccccccc | 5000     | STANDARD    | OPEN   | 40         | 0         |
    When I search trips with the following data
      | originCityId                         | destinationCityId                    | serviceDate |
      | 11111111-1111-1111-1111-111111111111 | 22222222-2222-2222-2222-222222222222 | seeded      |
    Then the response status is 200
    And I should see that trips that has been fetched with the following data
      | count |
      | 1     |
    And I should see that the following trips have been fetched
      | id                                   | status |
      | dddddddd-dddd-dddd-dddd-dddddddddddd | OPEN   |
