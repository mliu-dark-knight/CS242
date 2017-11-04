from bs4 import BeautifulSoup

class Project(object):
	def __init__(self, id=None, title=None, date=None, version=None, message=None, files=None):
		self.id = id
		self.title = title
		self.date = date
		self.version = version
		self.summary = message
		self.files = files

class File(object):
	def __init__(self, size=None, ftype=None, path=None, commits=None):
		self.size = size
		self.type = ftype
		self.path = path
		self.src = ' '.join(path.split('/'))
		self.commits = commits

class Commit(object):
	def __init__(self, revision=None, netid=None, message=None, date=None):
		self.number = revision
		self.author = netid
		self.info = message
		self.date = date


def file_type(fname):
	if fname.endswith('.png'):
		return 'image'
	if fname.endswith('.rb') or fname.endswith('.java'):
		return 'code'
	return 'resource'


def parse(svn_list, svn_log):
	titles = {'Assignment0': 'Getting Started',
			  'Assignment1.0': 'Chess', 'Assignment1.1': 'Chess', 'Assignment1.2': 'Chess',
			  'Assignment2.0': 'CS Air', 'Assignment2.1': 'CS Air'}
	data = {}
	soup = BeautifulSoup(open(svn_list), 'xml')
	prefix = 'mliu60/'
	for entry in soup.find_all('entry'):
		name = entry.find('name').text
		if '/' not in name:
			data[prefix + name] = Project(id=name[10:], title=titles[name], date=entry.find('date').text, version=entry.find('commit')['revision'])
		else:
			if entry['kind'] == 'dir':
				continue

			pair = name.split('/', 1)
			project_name = prefix + pair[0]
			file_name = prefix + name
			files = data[project_name].files
			if files == None:
				files = {}
			files[file_name] = File(size=entry.find('size').text, ftype=file_type(file_name), path=file_name)
			data[project_name].files = files

	soup = BeautifulSoup(open(svn_log), 'xml')
	for logentry in soup.find_all('logentry'):
		author = logentry.find('author').text
		date = logentry.find('date').text
		message = logentry.find('msg').text
		revision = logentry['revision']

		for path in logentry.find('paths').find_all('path'):
			if path['kind'] == 'dir':
				continue

			split = path.text.split('/')
			project_name = split[1] + '/' + split[2]
			file_name = path.text[1:]
			if file_name not in data[project_name].files:
				continue
			commits = data[project_name].files[file_name].commits
			if commits == None:
				commits = {}
			commits[revision] = Commit(revision=revision, netid=author, date=date, message=message)
			data[project_name].files[file_name].commits = commits

			if revision == data[project_name].version:
				data[project_name].summary = message

	return data

