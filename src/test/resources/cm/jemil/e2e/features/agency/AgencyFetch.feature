Feature: Agency Fetch
  As an admin user
  I want to fetch and manage transport agencies
  So that agencies can be onboarded onto the platform

  Background:
    Given I am connected as user id "admin-1" with the roles "ADMIN"

  Scenario: Fetch an agency by ID
    And I assume that the agencies with the following data are inside the database
      | id                                   | name            | countryCode | phoneNumber | licenseNumber | status | isDeleted | createdAt           |
      | a922f29c-5d6b-438d-aa17-a568d3be34c4 | Tresor voyages  | 237         | 699999999   | LIC-29350759  | ACTIVE | false     | 2025-04-10T11:51:50 |
      | dde1751a-04ff-4d46-ae0b-ee9cb16179ff | General voyages | 237         | 677999999   | LIC-43350760  | ACTIVE | false     | 2025-04-10T11:51:50 |
    When I fetch an agency identified by 'a922f29c-5d6b-438d-aa17-a568d3be34c4'
    Then I should see that the agency with the following data have been fetched
      | id                                   |
      | a922f29c-5d6b-438d-aa17-a568d3be34c4 |

  Scenario: Fetch all registered agencies
    And I assume that the agencies with the following data are inside the database
      | id                                   | name            | countryCode | phoneNumber | licenseNumber | status | isDeleted | createdAt           |
      | a922f29c-5d6b-438d-aa17-a568d3be34c4 | Tresor voyages  | 237         | 699999999   | LIC-29350759  | ACTIVE | false     | 2025-04-10T11:51:50 |
      | dde1751a-04ff-4d46-ae0b-ee9cb16179ff | General voyages | 237         | 677999999   | LIC-43350760  | ACTIVE | false     | 2025-04-10T11:51:50 |
    When I fetch all agencies with the following data
      | page | limit | city |
      | 0    | 10    |      |
    Then I should see that the agencies below are amongst the fetch ones
      | id                                   |
      | a922f29c-5d6b-438d-aa17-a568d3be34c4 |
      | dde1751a-04ff-4d46-ae0b-ee9cb16179ff |
