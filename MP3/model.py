import datetime
import re
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import desc

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///svn.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True
db = SQLAlchemy(app)

# table comment
class Comment(db.Model):
	__tablename__ = 'comment'
	id = db.Column(db.Integer, primary_key=True)
	content = db.Column(db.Text)
	file = db.Column(db.Text)
	date = db.Column(db.String(64))
	parent = db.Column(db.Integer, default=-1)

# comment tree with depth of 2
class Tree(object):
	def __init__(self, ID=None, content=None, date=None, children=None):
		self.ID = ID
		self.content = content
		self.date = date
		self.children = children

# table filter, contains replacement
class Filter(db.Model):
	__tablename__ = 'filter'
	id = db.Column(db.Integer, primary_key=True)
	red_flag = db.Column(db.String(32))
	replacement = db.Column(db.String(32))

# called when populating database
def populate_filter():
	phrases = ["Donald Trump", "Adolf Hitler", "Mao Zedong", "Jiang Zemin", "Xi Jinping"]
	for phrase in phrases:
		filt = Filter(red_flag=phrase, replacement="Badass")
		db.session.add(filt)
	db.session.commit()

# filter a comment
def filtering(sentence):
	sentence = sentence.encode("utf-8")
	filters = Filter.query
	for f in filters:
		sentence = re.compile(re.escape(f.red_flag), re.IGNORECASE).sub(f.replacement, sentence)
	return sentence

# add comment
def add_comment(content=None, file=None, parent=None):
	content, date = filtering(content), datetime.datetime.now()
	comment = Comment(content=content, file=file, parent=parent, date=date)
	db.session.add(comment)
	db.session.commit()
	return content, str(date), parent, comment.id

# retrieve comments of file
def get_comments(file=None):
	comments = [Tree(ID=comment.id, content=comment.content, date=comment.date) for comment in Comment.query.filter(Comment.file == file, Comment.parent == -1)]
	for comment in comments:
		comment.children = [Tree(ID=child.id, content=child.content, date=child.date) for child in Comment.query.filter(Comment.file == file, Comment.parent == comment.ID)]
	return comments


if __name__ == "__main__":
	db.create_all()
	populate_filter()

