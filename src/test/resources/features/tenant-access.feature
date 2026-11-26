@identity
Feature: Organization-scoped access and role administration
  As an organization administrator
  I want each user to act only inside their organization and within their role
  So that assets, incidents and reports remain isolated between tenants

  @US20
  Scenario Outline: An authenticated user operates only in its own organization
    Given a signed-in user of organization 7 with permissions "assets:view"
    When the user accesses organization <organization>
    Then the access result is "<result>"

    Examples:
      | organization | result    |
      | 7            | allowed   |
      | 8            | forbidden |

  @US20
  Scenario: A request without a valid session is rejected
    Given no signed-in user
    When the user accesses organization 7
    Then the access result is "unauthorized"

  @US21 @US43
  Scenario: A user manager assigns an operational role to a colleague
    Given a signed-in user of organization 7 with permissions "users:manage"
    And the role 3 is "operator"
    When the user assigns role 3 to user 12 of organization 7
    Then the access result is "allowed"

  @US43
  Scenario: A user manager cannot grant the administrator role
    Given a signed-in user of organization 7 with permissions "users:manage"
    And the role 2 is "administrator"
    When the user assigns role 2 to a new user of organization 7
    Then the access result is "forbidden"

  @US43
  Scenario: An administrator manager can grant the administrator role
    Given a signed-in user of organization 7 with permissions "users:manage,administrators:manage"
    And the role 2 is "administrator"
    When the user assigns role 2 to a new user of organization 7
    Then the access result is "allowed"

  @US43 @US44
  Scenario: A user cannot change its own role
    Given a signed-in user of organization 7 with permissions "users:manage"
    And the role 3 is "operator"
    When the user assigns role 3 to itself
    Then the access result is "forbidden"

  @US45
  Scenario: Internal owner queries require the workload key and keep the organization scope
    Given the identity owner knows organization 7 with 2 users
    When an internal client queries organization 7 users count with the workload key
    Then the internal answer is 2
    And a query with a browser token is rejected as unauthorized
