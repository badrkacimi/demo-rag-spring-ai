# Project RAG with Spring AI

RAG implementation with Spring AI 0.8.1

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Installation

1. **Clone the repository:**

    ```bash
    git clone https://github.com/badrkacimi/demo-rag-spring-ai.git
    ```

2. **Navigate to the project directory:**

    ```bash
    cd emo-rag-spring-ai
    ```

3. **Install dependencies:**

    Place your pdfs in resources/pdfs/

    ```bash
    docker compose up
    ```

## Usage

1. **Run the project:**

   Run the project in your editor or in command shell after generating the jar
   
   Check your postgres database, the user/password are in the compose yaml file
    ```bash
        http://localhost:5050/browser/
    ```

1. **Access the project:**

    Open a web browser and go to [http://localhost:9090/rag](http://localhost:9090/rag)]


## License

This project is licensed under the [MIT License](LICENSE).
