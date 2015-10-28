# coding:utf-8
import json as simplejson
import MySQLdb
from django.shortcuts import render
from django.http import HttpResponse
from form import UserForm,PostImageForm
from models import User
from django.db import connection

def index(request):
	return HttpResponse(u"This is here")

# 登录
def login(request):
	dict = {}
	resultData = {}
	if request.method == 'POST':
		username = request.POST.get('username')
		password = request.POST.get('password')
		gender = request.POST.get('gender')
		pushKey = request.POST.get('pushKey')
		cur = connection.cursor()
		query = 'select * from here_user where username = %s and password = %s'
		value = [username,password]
		cur.execute(query,value)
		user = cur.fetchall()
		if user:
			dict['errorMessage'] = "login_success"
			dict['status'] = "0"
			resultData['userid'] = user[0][0]
			resultData['username'] = user[0][1]
			resultData['password'] = user[0][2]
			resultData['gender'] = user[0][3]
			resultData['pushKey'] = user[0][4]
			resultData['birthday'] = user[0][5]
			if user[0][6]:
				resultData['avatar'] = user[0][6]
			else:
				resultData['avatar'] = ''
			resultData['nickname'] = user[0][7]
			dict['resultData'] = resultData
		else:
			dict['errorMessage'] = "no_such_user_or_password_is_invalid"
			dict['status'] = "8003"
		json = simplejson.dumps(dict)
		return HttpResponse(json)
	else:
		dict['errorMessage'] = "POST failed"
		dict['status'] = "8002"
		json  = simplejson.dumps(dict)
		return HttpResponse("POST failed")		
# 注册
def register(request):
	dict = {}
	resultData = {}
	if request.method == 'POST':
		username = request.POST.get('username')
		password = request.POST.get('password')
		gender = request.POST.get('gender')
		pushKey = request.POST.get('pushKey')
		birthday = request.POST.get('birthday')
		nickname = request.POST.get('nickname')
		if username and password :
			cur = connection.cursor()
			query = 'select * from here_user where username = %s'
			cur.execute(query,[username])
			userResult = cur.fetchall()
			if userResult:
				dict['errorMessage'] = "username_is_exist"
				dict['status'] = "8002"
			else:
				dict['errorMessage'] = "register_success"
				dict['status'] = "0"
				# 将用户数据存入数据库
				cursor = connection.cursor()
				query = "insert into here_user(username,password,gender,pushkey,birthday,nickname) values(%s,%s,%s,%s,%s,%s)"
				cursor.execute(query,[username,password,gender,pushKey,birthday,nickname])
				# 返回客户端用户数据
				resultData['username'] = username
				resultData['password'] = password
				resultData['pushKey'] = pushKey
				resultData['gender'] = gender
				resultData['birthday'] = birthday
				resultData['nickname'] = nickname
				dict['resultData'] = resultData
		else:
			dict['errorMessage'] = "username_or_password_invalid"
			dict['status'] = "8001"
		json = simplejson.dumps(dict)
		return HttpResponse(json)
	else:
		dict['errorMessage'] = "POST_FAILED"
		dict['status'] = "8001"
		json  = simplejson.dumps(dict)
		return HttpResponse("POST failed")

# 根据用户名，判断该用户是否已经注册
def checkUserIsExist(request):
	dict = {}
	resultData = {}
	if request.method == 'POST':
		username = request.POST.get('username')
		if username:
			cursor = connection.cursor()
			cursor.execute("select * from here_user where username=%s",[username])
			res = cursor.fetchall()
			if res:
				dict['status'] = 0
				dict['errorMessage'] = "user_is_exist"
			else:
				dict['status'] = 8003
				dict['errorMessage'] = "user_is_not_exist"

			resultData['objects'] = "objects"
			dict['resultData'] = resultData
			json = simplejson.dumps(dict)
			return HttpResponse(json)

		else:
			dict['errorMessage'] = "username_is_null"
			dict['status'] = 8001
			json  = simplejson.dumps(dict)
			return HttpResponse("POST failed")

	else:
		dict['errorMessage'] = "POST_FAILED"
		dict['status'] = 8001
		json  = simplejson.dumps(dict)
		return HttpResponse("POST failed")

# 上传用户的头像
def uploadAvatar(request):	
	dict = {}
	resultData = {}
	if request.method == 'POST':
		username  = request.POST.get('username')
		avatar = request.FILES.get('file')
		print avatar
		if username:
			user = User.objects.get(username = username)
			if avatar:
				user.avatar = avatar
				user.save()
				dict['errorMessage'] = "UPDATE_AVATAR_SUCCESS"
				dict['status'] = "0"
			else:
				dict['errorMessage'] = "AVATAR_IS_INVALID"
				dict['status'] = "8004"
		else:
			dict['errorMessage'] = "USERNAME_IS_INVALID"
			dict['status'] = "8005"

		json  = simplejson.dumps(dict)
		return HttpResponse(json)
	else:
		dict['errorMessage'] = "POST_FAILED"
		dict['status'] = "8001"
		return HttpResponse("POST failed")

# 修改用户信息
def modifyUserInfo(request):
	dict = {}
	resultData = {}
	if request.method == 'POST':
		username = request.POST.get('username')
		password = request.POST.get('password')
		gender = request.POST.get('gender')
		birthday = request.POST.get('birthday')
		nickname = request.POST.get('nickname')
		userid = request.POST.get('userid')
		if password and gender and birthday and nickname:
			cursor = connection.cursor()
			query = "update here_user set password = %s,gender = %s,birthday = %s,nickname = %s where id = %s"
			value = [password,gender,birthday,nickname,userid]
			cursor.execute(query,value)
			# 返回用户信息
			query = 'select * from here_user where id = %s'
			value = [userid]
			cursor.execute(query,value)
			user = cursor.fetchall()
			if user:
				dict['errorMessage'] = "modify_user_info_success"
				dict['status'] = "0"
				resultData['username'] = user[0][1]
				resultData['password'] = user[0][2]
				resultData['gender'] = user[0][3]
				resultData['pushKey'] = user[0][4]
				resultData['birthday'] = user[0][5]
				if user[0][6]:
					resultData['avatar'] = user[0][6]
				else:
					resultData['avatar'] = 'null'
				resultData['nickname'] = user[0][7]
				dict['resultData'] = resultData
			else:
				dict['errorMessage'] = "data_base_error"
				dict['status'] = "8005"
		else:
			dict['errorMessage'] = "no_such_user_or_password_is_invalid"
			dict['status'] = "8003"
			
		json = simplejson.dumps(dict)
		return HttpResponse(json)
	else:
		dict['errorMessage'] = "POST_FAILED"
		dict['status'] = "8001"
		json  = simplejson.dumps(dict)
		return HttpResponse("POST failed")

# 用户发帖上传位置信息以及内容
def updateUserPostLocation(request):
	dict = {}
	resultData = {}
	if request.method == 'POST':
		
		longitude = request.POST.get('longitude')
		latitude = request.POST.get('latitude')
		shareContent = request.POST.get('shareContent')
		userid = request.POST.get('userid')
		city = request.POST.get('city')
		postImageForm = PostImageForm(request.POST,request.FILES)
		if postImageForm.is_valid():
			image_one = request.FILES('image_one')
			if image_one == None:
				image_one = ''
			image_two = request.FILES('image_two')
			if image_two == None:
				image_two = ''
			image_three = request.FILES('image_three')
			if image_three == None:
				image_three = ''
			image_four = request.POST.FILES('image_four')
			if image_four == None:
				image_four = ''
		if longitude and latitude and user and city:
			cursor = connection.cursor()
			query = "insert into here_location(longitude,latitude,city,shareContent,userid,image_one,image_two,image_three,image_four) values(%s,%s,%s,%s,%s,%s,%s,%s,%s)"
			value = [longitude,latitude,city,shareContent,userid,image_one,image_two,image_three,image_four]
			cursor.execute(query,value)
			dict['errorMessage'] = "update_user_location_success"
			dict['status'] = "0"
			resultData['user'] = user
			resultData['longitude'] = longitude
			resultData['latitude'] = latitude
			resultData['city'] = city
			resultData['shareContent'] = shareContent
			resultData['image_one'] = image_one
			resultData['image_two'] = image_two
			resultData['image_three'] = image_three
			resultData['image_four'] = image_four
			resultData['like'] = 0
			dict['resultData'] = resultData
		else:
			dict['errorMessage'] = "username_or_password_invalid"
			dict['status'] = "8001"
		json = simplejson.dumps(dict)
		return HttpResponse(json)
	else:
		dict['errorMessage'] = "POST_FAILED"
		dict['status'] = "8001"
		json  = simplejson.dumps(dict)
		return HttpResponse("POST failed")
		
# 根据用户的地理经纬度，来获取他周围的定位信息
def getLocationByLocation(request):
	dict = {}
	resultData = []
	if request.method == 'POST':
		longitude = request.POST.get('longitude')
		latitude = request.POST.get('latitude')
		city = request.POST.get('city')
		cur = connection.cursor()
		query = "select * from Location where city=%s"
		cur.execute(query,[city])
		loc = cur.fetchall()
			