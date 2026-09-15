@scheduler @trip-generation
Feature: Trip generation scheduler (simulated tick)
  As the platform
  I want the nightly trip generation job to materialise a 14-day window
  So that passengers can search departures without manual trip creation

  Scenario: Simulated tick creates template trips and is idempotent
    Given I remember how many trips exist for the seeded schedule template
    When the trip generation scheduler tick is simulated
    Then trips generated from the seeded schedule template cover at least 14 service dates
    And I remember the trip count for the seeded schedule template
    When the trip generation scheduler tick is simulated again
    Then the trip count for the seeded schedule template did not increase
