import json
from flask import Flask, render_template, request, redirect
from utils.parse import *
from model import *

data = parse('static/data/svn_list.xml', 'static/data/svn_log.xml')

# home page
@app.route('/', methods=['GET'])
def home():
	return render_template('index.html')

# assignment with idx specified in url
@app.route('/assignment<idx>', methods=['GET'])
def assignment(idx):
	return render_template('assignment.html', project=data['mliu60/Assignment' + idx])

# comment with path and parent id specified in url, post only
@app.route('/comment/<path>/<parent>', methods=['POST'])
def comment(path, parent):
	print "post"
	split = path.split()
	project_name = split[0] + '/' + split[1]
	content, date, parent, id = add_comment(content=request.form["content" + parent], file='/'.join(split), parent=int(parent))
	return json.dumps({"content": content, "date": date, "parent": parent, "id": id})

# revision of file with path specified in url
@app.route('/revision/<path>', methods=['GET'])
def revision(path):
	split = path.split()
	path = '/'.join(split)
	project_name = split[0] + '/' + split[1]
	return render_template('file.html', file=data[project_name].files[path], comments=get_comments(file=path))

if __name__ == '__main__':
	app.run()