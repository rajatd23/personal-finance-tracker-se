from flask import Blueprint, request, jsonify
from flask_login import login_user, logout_user, current_user
from .models import User
from . import db

auth = Blueprint("auth", __name__)

@auth.route("/signup", methods=["POST"])
def signup():
    data = request.get_json()
    username = data.get("username")
    email = data.get("email")
    password = data.get("password")

    if User.query.filter_by(username=username).first() or User.query.filter_by(email=email).first():
        return jsonify({"message": "Username or email already exists"}), 400

    new_user = User(username=username, email=email)
    new_user.set_password(password)
    db.session.add(new_user)
    db.session.commit()

    return jsonify({"message": "User created successfully"}), 201

@auth.route("/login", methods=["POST"])
def login():
    data = request.get_json()
    user = User.query.filter_by(username=data.get("username")).first()

    if user and user.check_password(data.get("password")):
        login_user(user)
        return jsonify(
            {
                "message": "Logged in successfully",
                "user_id": user.id,
                "username": user.username,
                "email": user.email,
            }
        ), 200

    return jsonify({"message": "Invalid username or password"}), 401

@auth.route("/logout")  # Changed to POST for consistency
def logout():
    return jsonify({"message": "Logged out successfully"}), 200

@auth.route("/check_auth", methods=["GET"])
def check_auth():
    if current_user.is_authenticated:
        return jsonify(
            {
                "authenticated": True,
                "user_id": current_user.id,
                "username": current_user.username,
                "email": current_user.email,
            }
        ), 200
    return jsonify({"authenticated": False}), 401
