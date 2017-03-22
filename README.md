execute
mvn compile
mvn spring-boot:run

# Done
- use swagger for endpoint documentation/ presentation

# Tasks

## Grammar
- repair has attributes ... belongs to machine of customer
- tasks: check daily for all repairs with no payment send reminder
- view/ projection, filter attributes from overview
- reports: report for all repairs with no payment 

## Persistence
- persistent memory
- repository with findBy-method for belongs to
- mappedBy for symmetry, use cases?

## UI
- option/selection for belongs to attributes
- dynamic list for set attributes (5-10 input rows, additional with +-button)
- module- and component structure according to Angular tutorial
- Error handling, non-200-requests
- i18n
- type of input element based on attribute type
- validation

## Business Processes
- actions cause state transition
- based on BPMN
- adapter/ plugin for Activiti 

## Rules
- Exists- and All-Quantor logic
- translation to Drools (=Open Source) or similar (JRules/ facade resp. plugin)
- dependency check based on model

## Security
- authentication
- authorization
- spring-security stack, flexible, standardized

# Ideas
- output grammar from backend
