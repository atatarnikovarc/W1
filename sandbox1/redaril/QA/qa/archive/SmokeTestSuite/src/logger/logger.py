try:
	from win32com.client import Dispatch
except:
	pass
import os 

class Logger:
	_list = []
	_name = ''
	
	def Init(self, documentName, columnList):
		self._name = documentName
		self._name.lower()
		self._list = columnList
		
		if self._name.find('.html') != -1:
			self.proc = HTML_Logger()
			self.proc.Init(columnList)
		elif self._name.find('.xls') != -1:
			self.proc = XLS_Logger()
			self.proc.Init(documentName, columnList)

	
	def AddString(self, par_list):
		self.proc.AddString(par_list)

	def Save(self):
		self.proc.Save(self._name)


class HTML_Logger:
	_out = ''
	_columns = []
	
	def Init(self,  columnList):
		self._columns = columnList
		self._out = ''
	
	def AddString(self, par_list):
		self._out += '<tr>\n'
		for st in par_list:
			self._out += '<td>' + st + '</td>\n'
		self._out += '</tr>\n'
	
	def Save(self, documentName):
		content = ''
		try:
			fr = open(documentName, 'r')
			content = fr.read()
			fr.close()
		except IOError:
			content = '<html><body>'
			content += '<table border=\'1\' width=100%>\n' 
			content += '<tr>\n'
			for st in self._columns:
				content += '<td>' + st + '</td>\n'
			content += '</tr>\n'
		else:
			content = content[ : content.find('</table>')]
			#print content
		f = open(documentName, 'w')
		f.write(content + self._out + '</table>\n</body></html>')
		f.close()
		self._out = ''


"""from pyExcelerator import *

class XLS_Logger:

	def init (self,  columnList):
		self._laststr = 1
		self._workbook = Workbook()
		self._sheet = self._workbook.add_sheet('Logger')
		for i in range(0, len(columnList)):
			self._sheet.col(i).width = 0x1d00/2
		bold_font=Font()
		bold_font.bold=True
		bold_font.outline=True
		self.bold_style=XFStyle()
		self.bold_style.font=bold_font
		self.table_style=XFStyle()
		i =0
		for st in columnList:
			self._sheet.write(0, i, st, self.bold_style)
			i += 1


  	def AddString(self, par_list):
		i =0
		for st in par_list:
			self._sheet.write(self._laststr, i, st, self.table_style)
			i += 1
		self._laststr += 1

	def Save(self, documentName):
		self._workbook.save(documentName)
"""
class XLS_Logger:
	def Init (self, documentName, columnList):
		self.xlApp = Dispatch("Excel.Application")
		self.xlApp.Visible = 0
		self.freestr = 1
		if os.path.exists(documentName):
			self.wb = self.xlApp.Workbooks.Open(documentName)
			self.sh = self.wb.Worksheets(1)
			while str(self.sh.Cells(self.freestr, 1).Value)  != 'None':
				self.freestr += 1
		else:
			self.wb = self.xlApp.Workbooks.Add()
			self.sh = self.wb.Worksheets(1)
			self.AddString(columnList)
	
	def AddString(self, par_list):
		i = 1
		for col in par_list:
			self.sh.Cells(self.freestr, i).Value = col
			i += 1
		self.freestr += 1
	
	def Save(self, documentName):
		if os.path.exists(documentName):
			self.wb.Save()
		else:
			self.wb.SaveAs(documentName)
		self.xlApp.Quit()
		del self.xlApp





