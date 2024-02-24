# Exceptions

This document provides guidelines for naming exceptions.

### Base Exception (`exceptions.BaseException`)

- `BaseException`: Serves as the base class for all custom exceptions. It includes fields for `timestamp`, `status code`, `error message`, `details`, and suggested `remedy`. Exceptions extending from this class inherit these properties and can be easily serialized to JSON format using the `toJson()` method.

BaseException fields are as follows:
  - `timestamp`: The time at which the exception occurred.
  - `status code`: The HTTP status code.
  - `message`: A human-readable error message.
  - `details`: Additional details about the error.
  - `remedy`: Suggested remedy for the error.

## Directory Structure

The directory structure for exceptions is as follows:

 - `exceptions/`
   - `auth/`: Contains exceptions related to authentication.
   - `common/`: Contains exceptions that are common across the application.
   - `entity/`: Contains exceptions related to entities.
   - `parsing/`: Contains exceptions related to parsing.
   - `security/`: Contains exceptions related to security.

## Naming Conventions

When naming exceptions, it is important to use terms that are clear and descriptive. The following are some common terms that can be used to name exceptions:

- `Bad`: Use this term when the source of the error is a value that is not acceptable. For example, BadRequestException.

- `Conflict`: Use this term when the source of the error is a conflict between two or more values. For example, ConflictException.

- `Forbidden`: Use this term when the source of the error is a lack of permission. For example, ForbiddenException.

- `Invalid`: Use this term when the source of the error is a value that doesn't meet certain requirements. For example, InvalidFormatException.

- `Missing`: This term is best when the error arises from the absence of a value or element that is supposed to be there. For example, MissingResourceException.

- `Not`: It is used for conditions where something is not in expected state. For example, FileNotFoundException or NotAuthenticatedException.

- `Failed`: This term reflects that an operation could not be completed successfully. For example, WriteFailedException.

- `Unavailable`: Use this term when a network, service, resource, or similar is not available. For example, ServiceUnavailableException.

- `Insufficient`: Useful when some resource is not enough. For example, InsufficientMemoryException.
