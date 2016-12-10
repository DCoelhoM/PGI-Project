import MySQLdb
import bcrypt
import json
from flask import *
app = Flask(__name__)

@app.route('/register', methods=["GET", "POST"])
def register():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        email = data['email']
        name = data['name']
        password = data['password']
        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()
        sql_check_email = "SELECT * FROM users WHERE email='%s'" % (email)
        try:
            cursor.execute(sql_check_email)
            n_results = cursor.rowcount
            if n_results==0:
                #encrypted_pw = bcrypt.hashpw(bpassword, bcrypt.gensalt(10))
                #print encrypted_pw
                sql_register = "INSERT INTO users(name, email, encrypted_password) VALUES('%s','%s','%s')" % (name,email,password)
                try:
                    cursor.execute(sql_register)
                    db.commit()
                    response = { "success" : 1, "msg" : "Registered with success."}
                except:
                    db.rollback()
                    response = { "success" : 0, "msg" : "Error registering."}
            else:
               response = { "success" : 0, "msg" : "Email already registered."}
        except:
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        return json.dumps(response)
    else:
        response = {"success" : 0, "msg" : "Error."}
        return json.dumps(response)


@app.route('/login', methods=["GET", "POST"])
def login():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        email = data['email']
        password = data['password']
        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()
        sql_check_email = "SELECT * FROM users WHERE email='%s'" % (email)
        try:
            cursor.execute(sql_check_email)
            n_results = cursor.rowcount
            if n_results==1:
                result = cursor.fetchall()
                if (result[0][3]==password):
                    response = { "success" : 1, "msg" : "Login with success.", "id" : result[0][0], "name" : result[0][1], "email" : result[0][2]}
                else:
                    response = { "success" : 0, "msg" : "Wrong credentials."}
            else:
                response = { "success" : 0, "msg" : "Error user not found."}
        except:
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        return json.dumps(response)
    else:
        response = {"success" : 0, "msg" : "Error."}
        return json.dumps(response)


@app.route('/savelocation', methods=["GET", "POST"])
def savelocation():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        user_id = int(data['id'])
        name = data['name']
        longitude = float(data['longitude'])
        latitude = float(data['latitude'])
        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()
        sql_loc = "INSERT INTO locations(user_id, name, longitude, latitude) VALUES(%i,'%s',%f,%f)" % (user_id,name,longitude,latitude)
        try:
            cursor.execute(sql_loc)
            db.commit()
            response = { "success" : 1, "msg" : "Location saved with success."}
        except:
            db.rollback()
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        return json.dumps(response)
    else:
        response = {"success" : 0, "msg" : "Error."}
        return json.dumps(response)

@app.route('/mylocations', methods=["GET", "POST"])
def mylocations():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        user_id = int(data['user_id'])
        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()
        sql_check_email = "SELECT * FROM locations WHERE user_id=%i" % (user_id)
        try:
            cursor.execute(sql_check_email)
            n_results = cursor.rowcount
            if n_results>0:
                results = cursor.fetchall()
                locations = []
                for row in results:
                    loc_id = row[0]
                    name = row[2]
                    longitude = row[3]
                    latitude = row[4]
                    locations.append({"id": loc_id, "name" : name, "longitude" : str(longitude), "latitude" : str(latitude)})
                response = {"success" : 1, "locations" : locations}
            else:
                response = { "success" : 0, "msg" : "No locations found."}
        except:
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        return json.dumps(response)
    else:
        response = {"success" : 0, "msg" : "Error."}
        return json.dumps(response)


@app.route('/deletelocation', methods=["GET", "POST"])
def deletelocation():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        loc_id = int(data['loc_id'])
        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()
        sql_check_email = "DELETE FROM locations WHERE id=%i" % (loc_id)
        try:
            cursor.execute(sql_check_email)
            db.commit()
            response = {"success" : 1, "msg" : "Location deleted with success."}
        except:
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        return json.dumps(response)
    else:
        db.rollback()
        response = {"success" : 0, "msg" : "Error."}
        return json.dumps(response)


@app.route('/createrequest', methods=["GET", "POST"])
def createrequest():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        user_id = int(data['user_id'])
        title = data['title']
        desc = data['description']
        item_list = data['list']
        loc_id = int(data['loc_id'])
        deadline = data['deadline']

        #from datetime import datetime
        #datetime_object = datetime.strptime(deadline,'%Y-%m-%d %H:%M')
        #string = datetime.strftime('%Y-%m-%d %H:%M')

        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()

        sql_loc = "INSERT INTO requests(owner_id, title, description, loc_id, deadline) VALUES(%i,'%s','%s',%i,'%s')" % (user_id,title,desc,loc_id,deadline)
        try:
            cursor.execute(sql_loc)
            db.commit()
            req_id=cursor.lastrowid
            for i in range(len(item_list)):
                sql_item = "INSERT INTO items(request_id,info) VALUES(%i,'%s')" % (req_id,item_list[i]['item_'+str(i)])
                cursor.execute(sql_item)
            db.commit()
            response = { "success" : 1, "msg" : "Request created with success."}
        except:
            db.rollback()
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        return json.dumps(response)
    else:
        response = {"success" : 0, "msg" : "Error."}
        return json.dumps(response)

@app.route('/listrequests', methods=["GET", "POST"])
def listrequests():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        user_id = int(data['user_id'])
        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()
        sql_requests = "SELECT * FROM requests WHERE owner_id!=%i AND state='active'" % (user_id)
        try:
            cursor.execute(sql_requests)
            n_results = cursor.rowcount
            if n_results>0:
                results = cursor.fetchall()
                requests = []
                for row in results:
                    r_id = row[0]
                    owner_id = row[1]
                    title = row[2]
                    description = row[3]
                    loc_id = row[4]
                    deadline = row[6]
                    #Name
                    sql_owner = "SELECT name FROM users WHERE id=%i" % (owner_id)
                    cursor.execute(sql_owner)
                    owner_result = cursor.fetchall()
                    owner_name = owner_result[0][0]
                    #Feedback
                    sql_feedback = "SELECT feedback_owner FROM requests WHERE owner_id=%i AND state='ended' AND feedback_owner IS NOT NULL" % (owner_id)
                    cursor.execute(sql_feedback)
                    fb_result = cursor.fetchall()
                    feedback = "n"
                    if (len(fb_result)>0):
                        feedback = 0
                        for fb in fb_result:
                            feedback += fb[0]
                        feedback = feedback / len(fb_result)
                    #Location
                    sql_loc = "SELECT * FROM locations WHERE id=%i" % (loc_id)
                    cursor.execute(sql_loc)
                    loc_result =  cursor.fetchall()
                    longitude = loc_result[0][3]
                    latitude = loc_result[0][4]
                    #Items
                    sql_items = "SELECT * FROM items WHERE request_id=%i" % (r_id)
                    cursor.execute(sql_items)
                    items_result = cursor.fetchall()
                    items = []
                    for item in items_result:
                        items.append(item[2])

                    requests.append({"id": r_id, "owner": owner_name, "feedback": feedback,"title": title.decode('latin1'), "description": description.decode('latin1'), "list": items, "longitude": str(longitude), "latitude": str(latitude),"deadline": deadline.strftime('%Y-%m-%d %H:%M')})
                response = {"success" : 1, "msg" : "Requests found.","requests" : requests}
            else:
                response = { "success" : 0, "msg" : "No requests found."}
        except:
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        return json.dumps(response)
    else:
        response = {"success" : 0, "msg" : "Error."}
        return json.dumps(response)

@app.route('/listmyrequests', methods=["GET", "POST"])
def listmyrequests():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        user_id = int(data['user_id'])
        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()
        sql_requests = "SELECT * FROM requests WHERE owner_id=%i" % (user_id)
        try:
            cursor.execute(sql_requests)
            n_results = cursor.rowcount
            if n_results>0:
                results = cursor.fetchall()
                requests = []
                for row in results:
                    r_id = row[0]
                    title = row[2]
                    description = row[3]
                    loc_id = row[4]
                    created_at = row[5]
                    deadline = row[6]
                    state = row[7]
                    helper_id = row[8]
                    feedback = row[9]
                    feedback_helper = row[10]
                    helper_name = ""
                    if state != "active" and state != "canceled":
                        #Helper Name
                        sql_helper = "SELECT name FROM users WHERE id=%i" % (helper_id)
                        cursor.execute(sql_helper)
                        helper_result = cursor.fetchall()
                        helper_name = helper_result[0][0]
                    #Location
                    sql_loc = "SELECT * FROM locations WHERE id=%i" % (loc_id)
                    cursor.execute(sql_loc)
                    loc_result =  cursor.fetchall()
                    loc_name = loc_result[0][2]
                    #Items
                    sql_items = "SELECT * FROM items WHERE request_id=%i" % (r_id)
                    cursor.execute(sql_items)
                    items_result = cursor.fetchall()
                    items = []
                    for item in items_result:
                        items.append(item[2])
                    if state == "active":
                        requests.append({"id": r_id,"title": title.decode('latin1'), "description": description.decode('latin1'), "list": items, "location": loc_name, "created": created_at.strftime('%Y-%m-%d %H:%M') ,"deadline": deadline.strftime('%Y-%m-%d %H:%M'), "state": state})
                    else:
                        requests.append({"id": r_id,"title": title.decode('latin1'), "description": description.decode('latin1'), "list": items, "location": loc_name, "created": created_at.strftime('%Y-%m-%d %H:%M') ,"deadline": deadline.strftime('%Y-%m-%d %H:%M'), "state": state, "helper": helper_name, "feedback": feedback ,"feedback_helper": feedback_helper})
                response = {"success" : 1, "msg" : "success","requests" : requests}
            else:
                response = { "success" : 0, "msg" : "No requests found."}
        except:
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        return json.dumps(response)
    else:
        response = {"state" : 0, "msg" : "Error."}
        return json.dumps(response)

@app.route('/listmyrequests_helper', methods=["GET", "POST"])
def listmyrequests_helper():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        user_id = int(data['user_id'])
        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()
        sql_requests = "SELECT * FROM requests WHERE helper_id=%i" % (user_id)
        try:
            cursor.execute(sql_requests)
            n_results = cursor.rowcount
            if n_results>0:
                results = cursor.fetchall()
                requests = []
                for row in results:
                    r_id = row[0]
                    owner_id = row[1]
                    title = row[2]
                    description = row[3]
                    loc_id = row[4]
                    created_at = row[5]
                    deadline = row[6]
                    state = row[7]
                    feedback = row[9]
                    feedback_helper = row[10]
                    #Owner Name
                    sql_owner = "SELECT name FROM users WHERE id=%i" % (owner_id)
                    cursor.execute(sql_owner)
                    owner_result = cursor.fetchall()
                    owner_name = owner_result[0][0]
                    #Location
                    sql_loc = "SELECT * FROM locations WHERE id=%i" % (loc_id)
                    cursor.execute(sql_loc)
                    loc_result =  cursor.fetchall()
                    loc_name = loc_result[0][2]
                    #Items
                    sql_items = "SELECT * FROM items WHERE request_id=%i" % (r_id)
                    cursor.execute(sql_items)
                    items_result = cursor.fetchall()
                    items = []
                    for item in items_result:
                        items.append(item[2])
                    requests.append({"id": r_id, "owner_id": owner_id, "owner_name": owner_name,"title": title.decode('latin1'), "description": description.decode('latin1'), "list": items, "location": loc_name, "created": created_at.strftime('%Y-%m-%d %H:%M') ,"deadline": deadline.strftime('%Y-%m-%d %H:%M'), "state": state, "feedback": feedback ,"feedback_helper": feedback_helper})
                response = {"success" : 1, "msg" : "requests found","requests" : requests}
            else:
                response = { "success" : 0, "msg" : "No requests found."}
        except:
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        return json.dumps(response)
    else:
        response = {"state" : 0, "msg" : "Error."}
        return json.dumps(response)

@app.route('/acceptrequest', methods=["GET", "POST"])
def acceptrequest():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        user_id = int(data['user_id'])
        req_id = int(data['req_id'])
        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()
        sql_request = "SELECT state FROM requests WHERE id=%i FOR UPDATE" % (req_id)
        try:
            cursor.execute(sql_request)
            results = cursor.fetchall()
            if (results[0][0]=="active"):
                sql_accept = "UPDATE requests SET state='accepted', helper_id=%i WHERE id=%i" %(user_id, req_id)
                cursor.execute(sql_accept)
                db.commit()
                response = { "success" : 1, "msg" : "Request accepted with success."}
            else:
                db.rollback()
                response = { "success" : 0, "msg" : "Request already accepted."}
        except:
            db.rollback()
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        return json.dumps(response)
    else:
        response = {"success" : 0, "msg" : "Error."}
        return json.dumps(response)

@app.route('/requestinfo', methods=["GET", "POST"])
def requestinfo():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        req_id = int(data['req_id'])
        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()
        sql_requests = "SELECT owner_id, title, description, loc_id, created_at, deadline, state, helper_id,feedback_owner,feedback_helper FROM requests WHERE id=%i" % (req_id)
        print sql_requests
        try:
            cursor.execute(sql_requests)
            n_results = cursor.rowcount
            if n_results>0:
                results = cursor.fetchall()
                owner_id = results[0][0]
                title = results[0][1]
                description = results[0][2]
                loc_id = results[0][3]
                created_at = results[0][4]
                deadline = results[0][5]
                state = results[0][6]
                helper_id = results[0][7]
                feedback = results[0][8]
                if feedback == None:
                    feedback = "n"
                feedback_helper = results[0][9]
                if feedback_helper == None:
                    feedback_helper = "n"
                #Owner name
                owner_name = ""
                sql_owner = "SELECT name FROM users WHERE id=%i" % (owner_id)
                cursor.execute(sql_owner)
                owner_result = cursor.fetchall()
                owner_name = owner_result[0][0]
                #Items
                sql_items = "SELECT info FROM items WHERE request_id=%i" % (req_id)
                cursor.execute(sql_items)
                items_result = cursor.fetchall()
                items = []
                for item in items_result:
                    items.append(item[0])
                #Location
                sql_loc = "SELECT name FROM locations WHERE id=%i" % (loc_id)
                cursor.execute(sql_loc)
                loc_result =  cursor.fetchall()
                loc_name = loc_result[0][0]
                #Helper name
                helper_name = ""
                if state != "active" and state != "canceled":
                    sql_helper = "SELECT name FROM users WHERE id=%i" % (helper_id)
                    cursor.execute(sql_helper)
                    helper_result = cursor.fetchall()
                    helper_name = helper_result[0][0]
                print helper_name

                if state == "active":
                    response = {"success" : 1, "msg" : "Request found.", "owner": owner_name,"title": title.decode('latin1'), "description": description.decode('latin1'), "list": items, "location": loc_name, "created": created_at.strftime('%Y-%m-%d %H:%M') ,"deadline": deadline.strftime('%Y-%m-%d %H:%M'), "state": state}
                else:
                    response = {"success" : 1, "msg" : "Request found.", "owner": owner_name,"title": title.decode('latin1'), "description": description.decode('latin1'), "list": items, "location": loc_name, "created": created_at.strftime('%Y-%m-%d %H:%M') ,"deadline": deadline.strftime('%Y-%m-%d %H:%M'), "state": state, "helper": helper_name, "helper_id": helper_id, "feedback": feedback ,"feedback_helper": feedback_helper}
                print response
            else:
                response = { "success" : 0, "msg" : "Request not found."}
        except:
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        print response
        return json.dumps(response)
    else:
        response = {"state" : 0, "msg" : "Error."}
        return json.dumps(response)

@app.route("/givefeedbackhelper", methods=["GET", "POST"])
def givefeedbackhelper():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        req_id = int(data['req_id'])
        value = int(data['value'])
        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()
        sql_feedback = "UPDATE requests SET feedback_helper=%i WHERE id=%i" % (value, req_id)
        try:
            cursor.execute(sql_feedback)
            db.commit()
            response = { "success" : 1, "msg" : "Feedback changed with success."}
        except:
            db.rollback()
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        return json.dumps(response)
    else:
        response = {"success" : 0, "msg" : "Error."}
        return json.dumps(response)


@app.route("/cancelrequest", methods=["GET", "POST"])
def cancelrequest():
    response = {}
    if request.method == "POST":
        data = json.loads(request.data)
        req_id = int(data['req_id'])
        db = MySQLdb.connect("localhost","root","academica","helpie")
        cursor = db.cursor()
        sql_cancel = "UPDATE requests SET state='%s' WHERE id=%i" % ("canceled", req_id)
        print sql_cancel
        try:
            cursor.execute(sql_cancel)
            db.commit()
            response = { "success" : 1, "msg" : "Request canceled with success."}
        except:
            db.rollback()
            response = { "success" : 0, "msg" : "Error accessing DB."}
        db.close()
        return json.dumps(response)
    else:
        response = {"success" : 0, "msg" : "Error."}
        return json.dumps(response)

if __name__ == '__main__':
        app.run(host= '0.0.0.0',threaded=True, debug=True)
