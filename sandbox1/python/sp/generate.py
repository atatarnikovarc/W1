__author__ = 'a.tatarnikov'

from xml.etree.ElementTree import fromstring, ElementTree, Element
from datetime import datetime, date
import xml.etree.ElementTree, os, copy, pyodbc

def clearData(cursor):
    commands = ["delete from [dbo].[Barcode]", "delete from [dbo].[Goods]",
                "delete from [dbo].[Documents]"]
    for command in commands:
        cursor.execute(command)

def executeStoredProcedure(**kwargs):
    processed_parms = []

    for key in kwargs:
        curr_value = kwargs[key]
        if curr_value is not None:
            processed_parms.append(curr_value)

    xml_header = '<?xml version="1.0" encoding="Windows-1251"?>\n'
    formatted_xml = xml_header + xml.etree.ElementTree.tostring(kwargs.pop('xml'), encoding='utf-8', method='xml')
    processed_parms.append(formatted_xml)

    try:
        kwargs.pop('cursor').execute(processed_parms)
    except:
        print "can't execute sp"
    kwargs.pop('cursor').commit()

def executeDictionaryUpdate(cursor, xml_data):
    sp_params = {'command' : '{call dbo.DictionaryUpdate(?,?)}', 'LoadType': 'partitional', 'xml' : xml_data,
                 'cursor' : cursor}
    executeStoredProcedure(sp_params)

def executeDocumentWrite(cursor, xml_data):
    xml_header = '<?xml version="1.0" encoding="Windows-1251"?>\n'
    formatted_xml = xml_header + xml.etree.ElementTree.tostring(xml_data, encoding='utf-8', method='xml')

    command = "{call dbo.DocumentWrite(?)}"
    cursor.execute(command, formatted_xml)
    cursor.commit()

def getFileContent(filename):
    f = open(filename, "r")
    content = f.read()
    f.close()
    return content

def putAllDocuments(cursor):
    documents_path = "C:\\Users\\a.tatarnikov\\work\\svn\\QA\\testlink\\Client\\Fresh\\testdata"
    documents = []

    #get files to execute
    for path, subdirs, files in os.walk(documents_path):
        for name in files:
            curr_path = os.path.join(path, name)
            if ".xml" in curr_path:
                documents.append(curr_path)

    #put documents into db
    for doc in documents:
        print '{0}\r'.format(1),
        executeDocumentWrite(cursor, fromstring(getFileContent(doc)))

def main():
    #input parameters
    ip = "172.16.1.214"
    db_name = "CT5_1"
    db_login = "sa"
    db_password = "sa"
    goods_db_iterations = 1
    goods_db_records_per_iter = 1000
    goods_db_path = "./data/goods.xml"

    startTime = datetime.now().time()

    #prepare db connection
    cnxn = pyodbc.connect('DRIVER={SQL Server};SERVER=%s;DATABASE=%s;UID=%s;PWD=%s;CHARSET=UTF8'
                                                                            % (ip, db_name, db_login, db_password))
    cursor = cnxn.cursor()

    #get xml from file
    xml = fromstring(getFileContent(goods_db_path))

    #get 'Good' element copy
    good = xml.find('Good')

    #init clean and update with original goods db
    clearData(cursor)
    executeDictionaryUpdate(cursor, xml)
    putAllDocuments(cursor)

    #fullfill goods db table
    for j in range(0, goods_db_iterations):

        print '{0}\r'.format(j),
        current_xml = copy.deepcopy(xml)

        for i in range(0, goods_db_records_per_iter):
            newgood = copy.deepcopy(good)
            newgood.attrib["Code"] = str(j)+ "00000" + str(i)
            newgood.attrib["Name"] = "Tovar " + newgood.attrib["Code"]
            current_xml.insert(i, newgood)
        executeDictionaryUpdate(cursor, current_xml)

    #shutdown actions
    cursor.commit()
    cursor.close()


if __name__ == "__main__":
    main()