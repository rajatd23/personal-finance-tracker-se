import os

class Config:
    SECRET_KEY = os.environ.get("SECRET_KEY") or "the_secret_key"
    SQLALCHEMY_DATABASE_URI = (
        "mysql+pymysql://root:@localhost/finance_tracker"
    )
    SQLALCHEMY_TRACK_MODIFICATIONS = False
