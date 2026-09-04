@agency @route-search
Feature: Route Search
  As a passenger
  I want to search for available routes by city
  So that I can find buses between my origin and destination

  Background:
    Given I am connected as user id "admin-1" with the roles "ADMIN"

  Scenario: Search routes by origin and destination
    And I assume that the agencies with the following data are inside the database
      | id                                   | name         | countryCode | phoneNumber | licenseNumber | status | isDeleted | createdAt           |
      | 99999999-9999-9999-9999-999999999999 | Seed Transit | 237         | 699000000   | LIC-SEED-0001 | ACTIVE | false     | 2025-04-10T11:51:50 |
    And I assume that the route with the following data is inside the database
      | id                                   | agencyId                             | originCityId                         | destinationCityId                    | price | totalSeats |
      | aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa | 99999999-9999-9999-9999-999999999999 | 11111111-1111-1111-1111-111111111111 | 22222222-2222-2222-2222-222222222222 | 5000  | 40         |
    And I assume that the schedule with the following data is inside the database
      | id                                   | routeId                              | totalSeats | availableSeats |
      | bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb | aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa | 40         | 40             |
    When I search routes with the following data
      | originCityId                         | destinationCityId                    |
      | 11111111-1111-1111-1111-111111111111 | 22222222-2222-2222-2222-222222222222 |
    Then I should see that routes that has been fetched with the following data
      | count |
      | 1     |

  Scenario: Search routes with no results
    And I assume that the agencies with the following data are inside the database
      | id                                   | name         | countryCode | phoneNumber | licenseNumber | status | isDeleted | createdAt           |
      | 99999999-9999-9999-9999-999999999999 | Seed Transit | 237         | 699000000   | LIC-SEED-0001 | ACTIVE | false     | 2025-04-10T11:51:50 |
    And I assume that the route with the following data is inside the database
      | id                                   | agencyId                             | originCityId                         | destinationCityId                    | price | totalSeats |
      | aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa | 99999999-9999-9999-9999-999999999999 | 11111111-1111-1111-1111-111111111111 | 22222222-2222-2222-2222-222222222222 | 5000  | 40         |
    And I assume that the schedule with the following data is inside the database
      | id                                   | routeId                              | totalSeats | availableSeats |
      | bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb | aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa | 40         | 40             |
    When I search routes with the following data
      | originCityId                         | destinationCityId                    |
      | 33333333-3333-3333-3333-333333333333 | 44444444-4444-4444-4444-444444444444 |
    Then I should see that no routes has been fetched
