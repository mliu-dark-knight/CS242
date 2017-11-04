from utils.parse import *
from model import *
import unittest

class Test(unittest.TestCase):
	def test_revision(self):
		data = parse('static/data/test_list.xml', 'static/data/test_log.xml')
		dir_name = "mliu60/Assignment0"
		file1_name = "mliu60/Assignment0/src/lanSimulation/internals/Packet.java"
		file2_name = "mliu60/Assignment0/src/lanSimulation/Network.java"
		commits1 = data[dir_name].files[file1_name].commits
		commits2 = data[dir_name].files[file2_name].commits
		self.assertEqual(['7206', '1661', '1723'], commits1.keys())
		self.assertEqual(commits1.keys(), commits2.keys())

	def test_comment(self):
		add_comment(content="Root comment", file="comment", parent=-1)
		root = Comment.query.filter_by(file="comment").first()
		self.assertEqual("Root comment", root.content)
		add_comment(content="Subroot comment", file="comment", parent=root.id)
		subroot = Comment.query.filter(Comment.file == "comment", Comment.parent == root.id).first()
		self.assertEqual("Subroot comment", subroot.content)

	def test_injection(self):
		add_comment(content="';DROP TABLE comment", file="injection")
		comment = Comment.query.filter_by(file="injection").first()
		self.assertEqual("';DROP TABLE comment", comment.content)

	def test_filter(self):
		add_comment(content="Mao Zedong is a badass", file="filter")
		comment = Comment.query.filter_by(file="filter").first()
		self.assertEqual("Badass is a badass", comment.content)

		

def print_data(data):
	for d in data:
		print data[d].title, data[d].date, data[d].version, data[d].summary
		files = data[d].files
		for f in files:
			print files[f].size, files[f].path, files[f].type
			commits = files[f].commits
			for c in commits:
				print commits[c].number, commits[c].author, commits[c].info, commits[c].date



if __name__ == '__main__':
	unittest.main()
