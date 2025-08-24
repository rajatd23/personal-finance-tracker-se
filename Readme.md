Personal Finance Tracker
The Personal Finance Tracker is a mobile application designed to help users manage their finances by tracking expenses, budgeting, and setting reminders for recurring transactions. The app is built with Android Studio, using Java for the main code and XML for the UI. It interacts with a Flask REST API backend and a MySQL database to ensure secure and reliable data storage. Key features include easy date selection, calendar integration for reminders, and Firebase for user authentication.

Features
Expense Tracking: Allows users to log daily transactions with details like amount, category, and description.
Budget Management: Enables users to set budgets and view their expenses against these budgets.
Recurring Transactions: Supports adding recurring transactions and integrates with Google Calendar for monthly reminders.

Secure Data Storage: Data is stored in a MySQL database managed by a Flask REST API.
Responsive UI: Built with Android Studio, using XML for layout and Java for functionality.

Technologies Used
Android Studio (Java, XML) – for the frontend mobile application
Flask – for the backend REST API
MySQL – for data storage & User Authentication via Secure server


Installation
Clone the repository:

on Command:
https://github.com/Shubhamhingu/Personal_Finance_Tracker
cd personal-finance-tracker
Set up the backend:

Install dependencies:

pip install -r requirements.txt

Configure the MySQL database and update config.py with your database credentials.
You can use Xampp Server for the Database connection just need to create an empty Database of name finance_tracker and rest tables creation is already taken care of by the flask server.
Set up the Android project:

Open the project Android folder in Android Studio.
Sync the project with Gradle to resolve dependencies.
Change the Constants.java file where you need to update the ip-address given by the flask server.

and can use a Android Device or Emulator to Install the Apk and run the app.

