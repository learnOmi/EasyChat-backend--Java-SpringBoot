[![zread](https://img.shields.io/badge/Ask_Zread-_.svg?style=flat&color=00b0aa&labelColor=000000&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iMTYiIGhlaWdodD0iMTYiIHZpZXdCb3g9IjAgMCAxNiAxNiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTQuOTYxNTYgMS42MDAxSDIuMjQxNTZDMS44ODgxIDEuNjAwMSAxLjYwMTU2IDEuODg2NjQgMS42MDE1NiAyLjI0MDFWNC45NjAxQzEuNjAxNTYgNS4zMTM1NiAxLjg4ODEgNS42MDAxIDIuMjQxNTYgNS42MDAxSDQuOTYxNTZDNS4zMTUwMiA1LjYwMDEgNS42MDE1NiA1LjMxMzU2IDUuNjAxNTYgNC45NjAxVjIuMjQwMUM1LjYwMTU2IDEuODg2NjQgNS4zMTUwMiAxLjYwMDEgNC45NjE1NiAxLjYwMDFaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik00Ljk2MTU2IDEwLjM5OTlIMi4yNDE1NkMxLjg4ODEgMTAuMzk5OSAxLjYwMTU2IDEwLjY4NjQgMS42MDE1NiAxMS4wMzk5VjEzLjc1OTlDMS42MDE1NiAxNC4xMTM0IDEuODg4MSAxNC4zOTk5IDIuMjQxNTYgMTQuMzk5OUg0Ljk2MTU2QzUuMzE1MDIgMTQuMzk5OSA1LjYwMTU2IDE0LjExMzQgNS42MDE1NiAxMy43NTk5VjExLjAzOTlDNS42MDE1NiAxMC42ODY0IDUuMzE1MDIgMTAuMzk5OSA0Ljk2MTU2IDEwLjM5OTlaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik0xMy43NTg0IDEuNjAwMUgxMS4wMzg0QzEwLjY4NSAxLjYwMDEgMTAuMzk4NCAxLjg4NjY0IDEwLjM5ODQgMi4yNDAxVjQuOTYwMUMxMC4zOTg0IDUuMzEzNTYgMTAuNjg1IDUuNjAwMSAxMS4wMzg0IDUuNjAwMUgxMy43NTg0QzE0LjExMTkgNS42MDAxIDE0LjM5ODQgNS4zMTM1NiAxNC4zOTg0IDQuOTYwMVYyLjI0MDFDMTQuMzk4NCAxLjg4NjY0IDE0LjExMTkgMS42MDAxIDEzLjc1ODQgMS42MDAxWiIgZmlsbD0iI2ZmZiIvPgo8cGF0aCBkPSJNNCAxMkwxMiA0TDQgMTJaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik00IDEyTDEyIDQiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSIxLjUiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIvPgo8L3N2Zz4K&logoColor=ffffff)](https://zread.ai/learnOmi/EasyChat-backend--Java-SpringBoot)
# EasyChat Backend Project
 
## 📌 Project Overview
 
EasyChat is a real-time chat application backend system built with **Java Spring Boot**. This project adopts a modern technology architecture, providing complete core features including user authentication, real-time messaging, file transfer, and more. It is designed for building high-performance instant messaging platforms.
 
## 🛠️ Tech Stack
 
### Core Framework
- **Spring Boot 3.2.0** - Application framework
- **MyBatis 3.0.5** - Persistence framework
- **MySQL 8.0.33** - Relational database
 
### Real-time Communication
- **Netty 4.1.68.Final** - High-performance network communication framework
  - WebSocket server
  - Real-time message push
 
### Caching & Distributed Systems
- **Redis 3.0.0** - Caching and session management
- **Redisson 3.18.0** - Redis client
  - Pub/sub message distribution
  - Advanced features like distributed locks
 
### Utility Libraries
- **FastJSON 1.2.83** - JSON serialization
- **Apache Commons Lang3 3.12.0** - Common utilities
- **Apache Commons Codec 1.16.0** - Encoding/decoding tools
- **EasyCaptcha 1.6.2** - CAPTCHA generation
- **Nashorn 15.4** - JavaScript engine
 
## ✨ Core Features
 
### 🔐 Authentication & Authorization
- Token-based user session management
- Custom annotation interceptors for access control
- Admin role verification mechanism
 
### 💬 Real-time Messaging Engine
- WebSocket persistent connection management
- Channel context lifecycle management
- Redis pub/sub for multi-node message distribution
- Support for both private and group chats
 
### 👥 Core Business Services
- User registration and login
- Contact and friend relationship management
- Chat message persistence
- File upload and download
- Group chat creation and management
 
### 🗄️ Data Access Layer
- Generic MyBatis base mapper
- Optimized XML mapper configuration
 
### 🏗️ Infrastructure
- Multi-tier Redis caching strategy
- Global exception handling
- Application startup validation
- Unified DTO design pattern
 
## 📦 Project Structure
easychat/  
├── src/main/  
│   ├── java/      # Java source code  
│   └── resources/ # Configuration files  
├── pom.xml        # Maven dependency configuration  
└── README.md      # Project documentation  



## 🚀 Quick Start

The project uses Maven for build and dependency management, supporting one-click startup. For detailed configuration and startup guides, please refer to the project documentation.

---

This project demonstrates a typical architecture for modern Java web applications, combining the development convenience of Spring Boot, the high-performance network communication capabilities of Netty, and the distributed features of Redis. It is a complete enterprise-level instant messaging solution.
