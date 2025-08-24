from flask import Blueprint, request, jsonify
from flask_login import login_required, current_user
from .models import Transaction, db, User
from datetime import datetime

main = Blueprint('main', __name__)

@main.route('/add_transaction', methods=['POST'])

def add_transaction():
    data = request.get_json()
    new_transaction = Transaction(
        date=datetime.strptime(data['date'], '%Y-%m-%d').date(),
        description=data['description'],
        amount=float(data['amount']),
        category=data['category'],
        transaction_type=data['type'],
        group_size=data.get('group_size'),
        user_id=data['user_id']
    )
    db.session.add(new_transaction)
    db.session.commit()
    return jsonify({'message': 'Transaction added successfully'}), 201

@main.route('/get_transactions')

def get_transactions():
    user_id = request.args.get('user_id')
    transactions = Transaction.query.filter_by(user_id=user_id).all()
    return jsonify([{
        'id': t.id,
        'date': t.date.strftime('%Y-%m-%d'),
        'description': t.description,
        'amount': t.amount,
        'category': t.category,
        'type': t.transaction_type,
        'group_size': t.group_size
    } for t in transactions]), 200

@main.route('/update_transaction/<int:transaction_id>', methods=['PUT'])

def update_transaction(transaction_id):
    transaction = Transaction.query.get_or_404(transaction_id)
    if transaction.user_id != current_user.id:
        return jsonify({'message': 'Unauthorized'}), 403

    data = request.get_json()
    transaction.date = datetime.strptime(data.get('date', transaction.date.strftime('%Y-%m-%d')), '%Y-%m-%d').date()
    transaction.description = data.get('description', transaction.description)
    transaction.amount = float(data.get('amount', transaction.amount))
    transaction.category = data.get('category', transaction.category)
    transaction.transaction_type = data.get('type', transaction.transaction_type)
    transaction.group_size = data.get('group_size', transaction.group_size)

    db.session.commit()
    return jsonify({'message': 'Transaction updated successfully'}), 200

@main.route('/delete_transaction/<int:transaction_id>', methods=['DELETE'])
def delete_transaction(transaction_id):
    print("Hahaha")
    print(transaction_id)
    transaction = Transaction.query.get_or_404(transaction_id)

    db.session.delete(transaction)
    db.session.commit()
    return jsonify({'message': 'Transaction deleted successfully'}), 200

@main.route('/get_user_profile/<int:user_id>', methods=['GET'])
def get_user_profile(user_id):
    print(user_id)
    user = User.query.get_or_404(user_id)
    return jsonify({
        'username': user.username,
        'email': user.email,
        'monthly_budget': user.monthly_budget,
        'target_expense': user.target_expense
    }), 200

@main.route('/update_user_profile/<int:user_id>', methods=['PUT'])
def update_user_profile(user_id):
    print(user_id)
    user = User.query.get_or_404(user_id)
    data = request.get_json()

    user.username = data.get('username', user.username)
    user.email = data.get('email', user.email)
    user.monthly_budget = float(data.get('monthly_budget', user.monthly_budget))
    user.target_expense = float(data.get('target_expense', user.target_expense))

    db.session.commit()
    return jsonify({'message': 'Profile updated successfully'}), 200

