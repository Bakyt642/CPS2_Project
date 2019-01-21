import pymysql

class DatabaseManager():
    def __init__(self):
        self.conn = pymysql.connect(host='localhost', port=3306, user='root', passwd='Rado123!@#', db='itm')
        self.cur = self.conn.cursor()

    def add_del_update_db_record(self, sql_query, args=()):
        self.cur.execute(sql_query, args)
        self.conn.commit()
        return

    def __del__(self):
        self.cur.close()
        self.conn.close()