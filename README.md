# Bookstore API Test Automation Framework

A comprehensive API automation testing framework for the FakeRestAPI Bookstore, built with Java, RestAssured, TestNG and ExtentReports following industry best practices.

## Project Structure

```
bookstore-api-tests/
├── src/
│   ├── main/java/
│   │   └── com/bookstore/
│   │       ├── models/          # POJOs for API entities
│   │       ├── clients/         # API client classes
│   │       ├── utils/           # Utility classes
│   │       └── config/          # Configuration management
│   └── test/
│       ├── java/
│       │   └── com/bookstore/
│       │       ├── tests/
│       │       │   ├── books/   # Books API tests
│       │       │   └── authors/ # Authors API tests
│       │       └── base/        # Base test classes
│       └── resources/
│           ├── config*.properties
│           ├── testng.xml
│           └── logback-test.xml
├── .github/workflows/           # CI/CD pipeline
├── pom.xml                      # Maven configuration
└── README.md
```

## Features

- **Comprehensive Test Coverage**: GET, POST, PUT, DELETE operations for Books and Authors APIs
- **Clean Architecture**: Follows SOLID principles with proper separation of concerns
- **Detailed Reporting**: ExtentReports with comprehensive HTML reports and step-by-step execution details
- **Sequential Execution**: Reliable test execution with proper test isolation
- **CI/CD Integration**: GitHub Actions pipeline with automated report generation
- **Flexible Configuration**: Environment-specific configurations
- **Robust Error Handling**: Comprehensive negative testing scenarios

## Prerequisites

- **Java 11** or higher
- **Maven 3.8+**
- **Git**
- **IDE** (IntelliJ IDEA, Eclipse, VS Code)

## Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd bookstore-api-tests
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Run All Tests
```bash
mvn clean test
```

**Expected Result:** Some tests will fail (approximately 27% failure rate). This is expected and indicates that the API does not follow REST standards properly.

### 4. View Test Results
- **Surefire Reports:** `target/surefire-reports/index.html`
- **ExtentReports:** `test-output/ExtentReports/BookstoreAPI_TestReport_[timestamp].html`

### 5. Understanding Test Failures
Test failures indicate areas where the API needs improvement:
- **400 Bad Request** expected but **200 OK** received → Validation issues
- **404 Not Found** expected but **200 OK** received → Resource management issues

These failures provide valuable feedback for API developers to implement proper REST standards.

## Configuration

### Environment Configuration
Edit `src/test/resources/config.properties` for default settings:

```properties
# API Configuration
api.base.url=https://fakerestapi.azurewebsites.net
api.version=v1
api.request.timeout=30000

# Test Configuration
test.environment=dev
test.logging.enabled=true
debug.mode=false
```

### Environment-Specific Configs
- `config-dev.properties` - Development environment
- `config-prod.properties` - Production environment

## Test Framework Details

### Test Classes Structure

**Authors API Tests:**
- `AuthorsGetTests.java` - Retrieval operations (8 tests)
- `AuthorsPostTests.java` - Creation operations (9 tests) - **Tests proper validation**
- `AuthorsPutTests.java` - Update operations (6 tests) - **Tests proper validation** 
- `AuthorsDeleteTests.java` - Deletion operations (6 tests) - **Tests proper validation**

**Books API Tests:**
- `BooksGetTests.java` - Retrieval operations
- `BooksPostTests.java` - Creation operations
- `BooksPutTests.java` - Update operations - **Tests proper validation**
- `BooksDeleteTests.java` - Deletion operations - **Tests proper validation**

### Framework Components

**Models** (`src/main/java/com/bookstore/models/`)
- `Author.java` - Author entity with Lombok annotations
- `Book.java` - Book entity with comprehensive validation

**API Clients** (`src/main/java/com/bookstore/clients/`)
- `AuthorsApiClient.java` - Authors API operations with error handling
- `BooksApiClient.java` - Books API operations

**Utilities** (`src/main/java/com/bookstore/utils/`)
- `ResponseValidator.java` - Custom validation methods adapted for API issues
- `TestDataGenerator.java` - Generates realistic test data
- `DateUtils.java` - Date formatting and parsing
- `ExtentManager.java` - ExtentReports configuration and management

## Running Tests

### Run All Tests
```bash
# Run complete test suite
mvn clean test

# Run with detailed output
mvn clean test -X
```

### Run Specific Test Suites
```bash
# Books API tests only
mvn test -Dtest="com.bookstore.tests.books.*"

# Authors API tests only
mvn test -Dtest="com.bookstore.tests.authors.*"

# Specific test class
mvn test -Dtest="BooksGetTests"
```

### Run with Different Environments
```bash
mvn test -Denv=prod
```

### Run with Custom TestNG Suite
```bash
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
```

### Optional: Enable Parallel Execution
To enable parallel execution for faster test runs, you can modify the TestNG XML configuration:

```xml
<suite name="Bookstore API Test Suite" verbose="1" parallel="methods" thread-count="3">
```

Or run with Maven parameters:
```bash
mvn test -Dparallel=methods -DthreadCount=3
```

**Note**: Parallel execution is currently disabled to ensure test stability and proper isolation.

## Test Reports

### ExtentReports
```bash
# Run tests and generate report
mvn clean test

# Reports are automatically generated in:
# test-output/ExtentReports/BookstoreAPI_TestReport_[timestamp].html
```

Reports are generated in `test-output/ExtentReports/`

### ExtentReport Features
- **Comprehensive HTML Reports** with modern UI and responsive design
- **Test execution timeline** with detailed steps and logs
- **API request/response details** for debugging
- **Categorized test results** by feature and test class
- **System information** including environment and configuration
- **Screenshots and attachments** for failed tests
- **Real-time report generation** during test execution

### Viewing Reports
1. Navigate to `test-output/ExtentReports/`
2. Open the latest `BookstoreAPI_TestReport_[timestamp].html` file in any web browser
3. No server setup required - reports work directly from file system

### Report Highlights
- **Dashboard Overview**: Test summary with pass/fail statistics
- **Test Details**: Step-by-step execution with logs and screenshots
- **Categories**: Tests organized by API endpoints (Authors, Books)
- **Timeline**: Chronological view of test execution
- **System Info**: Environment details and test configuration

## Known API Issues & Test Status

During testing, several issues were identified with the FakeRestAPI implementation. The tests are designed to validate proper REST API behavior and will fail when the API doesn't meet standards:

### 1. **Input Validation Issues**
- **Problem**: API accepts invalid data (empty strings, null values, negative numbers) and returns `200` instead of `400`
- **Test Behavior**: Tests expect `400 Bad Request` and will fail when API returns `200`
- **Examples**: 
  - Empty first/last names should return `400`
  - Null book IDs should return `400`
  - Negative book IDs should return `400`

### 2. **Resource Management Issues**
- **Problem**: API returns `200` instead of `404` for non-existent resources
- **Test Behavior**: Tests expect `404 Not Found` and will fail when API returns `200`
- **Examples**:
  - DELETE operations on non-existent authors should return `404`
  - PUT operations on non-existent authors should return `404`

### 3. **Data Integrity Issues**
- **Problem**: Newly created entities return ID as `0` instead of auto-generated positive values
- **Test Behavior**: Tests validate proper ID generation and may fail for invalid IDs
- **Impact**: Tests ensure proper resource creation standards

### Current Test Status
```
Total Tests: 66
Passed: 48 (73%)
Failed: 18 (27%)
Reason for Failures: API does not implement proper REST standards
```

The failing tests indicate areas where the API needs improvement to meet REST API standards.

### Recommendations for API Improvement
1. Implement proper input validation returning HTTP 400 for invalid data
2. Return HTTP 404 for non-existent resources
3. Generate proper auto-incremented IDs for new entities
4. Preserve null values instead of converting to 0
5. Add comprehensive error messages for debugging

### Example Test Implementation

**Current Test Approach (Validates REST Standards):**
```java
@Test
public void testCreateAuthor_EmptyFirstName_ReturnsError() {
    // Given: Invalid data
    Author invalidAuthor = Author.builder()
        .firstName("")
        .lastName("TestAuthor")
        .idBook(1)
        .build();
    
    // When: Send request
    Response response = authorsApiClient.createAuthor(invalidAuthor);
    
    // Then: Expect proper validation (will fail if API doesn't follow standards)
    ResponseValidator.validateClientError(response); // Expects 400, fails if API returns 200
}
```

**Test Results:**
- This test will FAIL because the API returns `200` instead of `400`
- The failure indicates that the API needs to implement proper validation
- Failed tests provide clear evidence of API non-compliance with REST standards

### TestNG Reports
Standard TestNG reports are available in `target/surefire-reports/`

## Test Coverage

### Books API Tests
- **GET** `/api/v1/Books` - Retrieve all books
- **GET** `/api/v1/Books/{id}` - Retrieve book by ID
- **POST** `/api/v1/Books` - Create new book
- **PUT** `/api/v1/Books/{id}` - Update existing book
- **DELETE** `/api/v1/Books/{id}` - Delete book

### Authors API Tests
- **GET** `/api/v1/Authors` - Retrieve all authors
- **GET** `/api/v1/Authors/{id}` - Retrieve author by ID
- **GET** `/api/v1/Authors/authors/books/{idBook}` - Get authors by book
- **POST** `/api/v1/Authors` - Create new author
- **PUT** `/api/v1/Authors/{id}` - Update existing author
- **DELETE** `/api/v1/Authors/{id}` - Delete author

### Test Scenarios Covered
- **Happy Path**: Valid data and successful operations
- **Negative Testing**: Invalid data, missing fields, edge cases
- **Error Handling**: 404, 400, 422 responses
- **Data Validation**: Response structure and data types

## CI/CD Pipeline

### GitHub Actions Features
- **Automated Testing**: Runs on every push and PR
- **Sequential Execution**: Reliable and stable test execution
- **Report Generation**: Automatic ExtentReports generation
- **Unified Artifacts**: Single artifact containing all test results, reports, and logs
- **Scheduled Runs**: Daily automated test execution

### Pipeline Triggers
- Push to `main` or `develop` branches
- Pull requests to `main`
- Daily scheduled runs (2 AM UTC)
- Manual workflow dispatch

## Development Guidelines

### Adding New Tests
1. Create test class in appropriate package (`books` or `authors`)
2. Extend `BaseTest` class
3. Use ExtentReports logging methods for detailed reporting
4. Follow naming convention: `test{Operation}_{Scenario}_{ExpectedResult}`
5. Add comprehensive assertions and logging

### Code Quality Standards
- **Clean Code**: Meaningful names, small methods, clear intent
- **SOLID Principles**: Single responsibility, dependency injection
- **Documentation**: Comprehensive JavaDoc for all public methods
- **Error Handling**: Proper exception handling and logging
- **Test Data**: Use TestDataGenerator for consistent test data
- **Reporting**: Use ExtentManager for consistent test reporting

### ExtentReports Integration
```java
// In test methods:
ExtentManager.logInfo("Test step information");
ExtentManager.logPass("Successful validation");
ExtentManager.logWarning("Warning about API behavior");
ExtentManager.logFail("Test failure details");
ExtentManager.assignCategory("API Category");
```

## Troubleshooting

### Common Issues

**API Connection Issues**
```bash
# Check API availability
curl -I https://fakerestapi.azurewebsites.net/api/v1/Books
```

**Maven Build Issues**
```bash
# Clean and reinstall dependencies
mvn clean install -U
```

**ExtentReports Issues**
```bash
# Check if reports are generated
ls -la test-output/ExtentReports/

# If reports are empty, check logs:
cat target/logs/test-execution.log
```

**Java Version Issues**
```bash
# Check Java version
java -version
mvn -version
```

### Logs Location
- Test execution logs: `target/logs/test-execution.log`
- Full logs: `target/logs/bookstore-api-tests.log`



## Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-test-suite`
3. Follow coding standards and add tests
4. Commit changes: `git commit -am 'Add new test suite'`
5. Push to branch: `git push origin feature/new-test-suite`
6. Create Pull Request

---
