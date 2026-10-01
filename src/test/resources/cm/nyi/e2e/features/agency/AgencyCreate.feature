@agency @registration
Feature: Agency Registration
  As an admin user
  I want to register and manage transport agencies
  So that agencies can be onboarded onto the platform

  Background:
    Given I am connected as user id "admin-1" with the roles "ADMIN"

  Scenario: Register a new agency successfully
    And I assume that the agency with the following data are not inside the database
      | id                                   |
      | f7f617c7-2916-4822-a287-216f8eb77602 |
    When I create an agency with the following data
      | name           | countryCode | number    | licenseNumber |
      | Global Voyages | 237         | 653492410 | LIC-29304759  |
    Then I should see that the agency has been created

  Scenario: Reject registration with invalid phone number
    When I try to create an agency with the following data
      | name          | countryCode | number | licenseNumber |
      | Kayla Voyages | 123         |        | LIC-33304759  |
    Then the last request failed with the http status "BAD_REQUEST" and error code "PHONE_400_001"

  Scenario: Reject registration when not authenticated
    Given I am not authenticated
    When I try to create an agency with the following data
      | name            | countryCode | number    | licenseNumber |
      | No Auth Voyages | 237         | 611111111 | LIC-12345678  |
    Then the response status is 401

  Scenario: Add a route to an existing agency
    And I assume that the agencies with the following data are inside the database
      | id                                   | name            | countryCode | phoneNumber | licenseNumber | status | isDeleted | createdAt           |
      | a922f29c-5d6b-438d-aa17-a568d3be34c4 | Tresor voyages  | 237         | 699999999   | LIC-29350759  | ACTIVE | false     | 2025-04-10T11:51:50 |
      | dde1751a-04ff-4d46-ae0b-ee9cb16179ff | General voyages | 237         | 677999999   | LIC-43350760  | ACTIVE | false     | 2025-04-10T11:51:50 |
    When I add a route to an agency identified by 'a922f29c-5d6b-438d-aa17-a568d3be34c4'
      | originCityId                         | destinationCityId                    | price | totalSeats |
      | 11111111-1111-1111-1111-111111111111 | 22222222-2222-2222-2222-222222222222 | 5000  | 50         |
    Then I should see that the route has been created
