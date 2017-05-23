package require tcltest 2.0
namespace import ::tcltest::*

proc init {} {
global templateFolder htmlFolder
# templateFolder - input data - folder with "templates" (pixels info)
# htmlFolder - output data - apache htdocs folder
set templateFolder "D:\\WorkSpace\\eclipse_workspace\\makePixel155\\pixels\\Templates\\"
set htmlFolder "C:\\Program Files\\Apache Software Foundation\\Apache2.2\\htdocs\\htmlTemplates\\"
}

proc parseTemplateFile { tFile } {
global Config templateFolder

	set templateFileName $templateFolder
	append templateFileName $tFile
	puts "templateFileName= $templateFileName"

	if { [catch { set ft [open $templateFileName] } fid] } {
		puts stderr $fid
		exit
	} else {
		while { ![eof $ft] } {
			gets $ft line
			if { [regexp #+ $line] && ( [string first "PIXEL" $line] != -1 ) } {
				set Config(pixelType) [string range $line 1 [string first "PIXEL" $line]-2]
				puts "pixelType= $Config(pixelType)"
			}
			if { ![regexp #+ $line] && ( $line ne "" ) } {
				set param [string range $line 0 [string first "=" $line]-1]
				set value [string range $line [string first "=" $line]+2 end]
				set Config($param) $value
			}
		} ;# //end while
		close $ft;

		if { $Config(pixelType) == "DATA" } {
			puts "DATA pixel"
			createDataPixelHTML
		} elseif { $Config(pixelType) == "CAMPAIGN" } {
			puts "CAMPAIGN pixel"
			createCampaignPixelHTML
		} else {
			puts stderr "ERROR: wrong template"
			exit
		}

	}

}

proc createCampaignPixelTemplate_SCRIPT {} {
global Config htmlFolder

	set htmlFileName $htmlFolder
	append htmlFileName $Config(pixelType) "_" $Config(id) "_SCRIPT.html" 
	puts "htmlFileName= $htmlFileName"
	
	if { $Config(isHttps) == "Y" } {
		set prot "https"
	} elseif { $Config(isHttps) == "N" } {
		set prot "http"
	} else {
		puts "ERROR: Wrong value of isHttps"
	}
	
	if { $Config(pixelFormat) == "I" } {
		set type "frame"
	} elseif { $Config(pixelFormat) == "S" } {
		set type "script"
	} else {
		puts "ERROR: Wrong value of pixelFormat"
	}

	set fHtml [open $htmlFileName w]
	set content "\
		<HTML><HEAD>\r\n\
		<TITLE>$Config(ruleSelection)</TITLE>\r\n\
		</HEAD><BODY>\r\n\
		<h1>$Config(pixelType) Iframe Pixel (id=$Config(id)) </h1>\r\n\
		<script type=\"text/javascript\">\r\n\
		_prot=\"$prot\";\r\n\
		_pixel=$Config(id);\r\n\
		_ch=\"\";\r\n\
		_type=\"$type\";\r\n\
		</script>\r\n\
		<script type=\"text/javascript\" src=\"$prot://p0.raasnet.com/partners/parts.js\"></script>\r\n\
		</BODY></HTML>\r\n"
	puts $fHtml $content
	close $fHtml
}

proc createCampaignPixelTemplate_DOCUMENT_WRITE {} {
global Config htmlFolder

	if { $Config(generateNdlNdr) == "Y" } {
		set NdlNdr "&ndl=http%3A%2F%2Fdomain.shopping.com%2Fcommon%2Fdart_wrapper_001.html%3F%2Fadj%2Fgtr.forsale%2Fdublin%253Bl2%253D%253Bkw%253D%253Bpos%253D%253Bsz%253D728x90%252C468x60%253Btile%253D1%253Bposting_cat%253D1958%253Bpage_type%253Dposting_static%253B&ndr=http%3A%2F%2Fsearch.aol.com%2Faol%2Fsearch%3Fs_it%3Dcomsearch40%26q%3Dcamping%2Bocean%2Bcity"
	} elseif { $Config(generateNdlNdr) == "N" } {
		set NdlNdr ""
	} else {
		puts "ERROR: Wrong value of generateNdlNdr"
	}
	
	if { $Config(isHttps) == "Y" } {
		set prot "https"
		set port "9410"
	} elseif { $Config(isHttps) == "N" } {
		set prot "http"
		set port "8080"
	} else {
		puts "ERROR: Wrong value of isHttps"
	}
	
	set htmlFileName $htmlFolder
	append htmlFileName $Config(pixelType) "_" $Config(id) "_DOCUMENT_WRITE.html" 
	puts "htmlFileName= $htmlFileName"
	set fHtml [open $htmlFileName w]
	set content "\
		<HTML><HEAD>\r\n\
		<TITLE>$Config(ruleSelection)</TITLE>\r\n\
		</HEAD><BODY>\r\n\
		<h1>$Config(pixelType) pixel with tag img (id=$Config(id)) </h1>\r\n\
		<!--\r\n\
		NOTE: WE DO NOT NEED NDR/NDL section because image tag means that script is disabled on the publisher site.\r\n\
		In this case, we're trying to get the publisher URL from the header\r\n\
		referee and record it to NDL, and send it to IC\r\n\
		-->\r\n\
		<script>document.write('<img width=\"0\" height=\"0\" src=\"$prot://p.raasnet.com:$port/partners/universal/in?pid=$Config(id)&t=[string tolower $Config(pixelFormat)]$NdlNdr\">');</script>\r\n\
		</BODY></HTML>\r\n"
	puts $fHtml $content
	close $fHtml
}

proc createCampaignPixelTemplate_IMG {} {
global Config htmlFolder

	if { $Config(generateNdlNdr) == "Y" } {
		set NdlNdr "&ndr=http%3A%2F%2Fdomain.shopping.com%2Fcommon%2Fdart_wrapper_001.html%3F%2Fadj%2Fgtr.forsale%2Fdublin%253Bl2%253D%253Bkw%253D%253Bpos%253D%253Bsz%253D728x90%252C468x60%253Btile%253D1%253Bposting_cat%253D1958%253Bpage_type%253Dposting_static%253B&ndl=http%3A%2F%2Fsearch.aol.com%2Faol%2Fsearch%3Fs_it%3Dcomsearch40%26q%3Dcamping%2Bocean%2Bcity"
	} elseif { $Config(generateNdlNdr) == "N" } {
		set NdlNdr ""
	} else {
		puts "ERROR: Wrong value of generateNdlNdr"
	}

	if { $Config(isHttps) == "Y" } {
		set prot "https"
		set port "9410"
	} elseif { $Config(isHttps) == "N" } {
		set prot "http"
		set port "8080"
	} else {
		puts "ERROR: Wrong value of isHttps"
	}

	set htmlFileName $htmlFolder
	append htmlFileName $Config(pixelType) "_" $Config(id) "_IMG.html" 
	puts "htmlFileName= $htmlFileName"
	set fHtml [open $htmlFileName w]
	set content "\
		<HTML><HEAD>\r\n\
		<TITLE>$Config(ruleSelection)</TITLE>\r\n\
		</HEAD><BODY>\r\n\
		<h1>$Config(pixelType) pixel with tag img (id=$Config(id)) </h1>\r\n\
		<!--\r\n\
		NOTE: WE DO NOT NEED NDR/NDL section because image tag means that script is disabled on the publisher site.\r\n\
		In this case, we're trying to get the publisher URL from the header\r\n\
		referee and record it to NDL, and send it to IC\r\n\
		-->\r\n\
		<img width=\"0\" height=\"0\" src=\"$prot://p.raasnet.com:$port/partners/universal/in?pid=$Config(id)&t=[string tolower $Config(pixelFormat)]$NdlNdr\">\r\n\
		</BODY></HTML>\r\n"
	puts $fHtml $content
	close $fHtml
}

proc createDataPixelTemlate_SCRIPT {} {
global Config htmlFolder

	if { $Config(isHttps) == "Y" } {
		set prot "https"
	} elseif { $Config(isHttps) == "N" } {
		set prot "http"
	} else {
		puts "ERROR: Wrong value of isHttps"
		exit
	}
	
	if { $Config(dataPixelFormat) == "I" } {
		set type "image"
	} elseif { $Config(dataPixelFormat) == "S" } {
		set type "script"
	} elseif { $Config(dataPixelFormat) == "F" } {
		set type "frame"
	} else {
		puts "ERROR: Wrong value of pixelFormat"
		exit
	}
	
	set htmlFileName $htmlFolder
	append htmlFileName $Config(pixelType) "_" $Config(id) "_SCRIPT_" $Config(dataPixelFormat) ".html" 
	puts "htmlFileName= $htmlFileName"
	set fHtml [open $htmlFileName w]
	set content "\
		<HTML><HEAD>\r\n\
		<TITLE>$Config(ruleSelection)</TITLE>\r\n\
		</HEAD><BODY>\r\n\
		<h1>$Config(pixelType) (id=$Config(id)) </h1>\r\n\
		<!-- NOTE: t=i image pixel, t=s script!!! -->\r\n\
		<script type=\"text/javascript\">\r\n\
		_prot=\"$prot\";\r\n\
		_pixel=$Config(id);\r\n\
		_ch=\"\";\r\n\
		_type=\"$type\";\r\n\
		</script>\r\n\
		<script type=\"text/javascript\" src=\"$prot://p0.raasnet.com/partners/parts.js\"></script>\r\n\
		</BODY></HTML>\r\n"
	puts $fHtml $content
	close $fHtml
}

proc createDataPixelTemlate_IMG { NdlNdr } {
global Config htmlFolder

	set htmlFileName $htmlFolder
	if { $NdlNdr == "Y" } {
		set NdlNdr "&ndl=http%3A%2F%2Fdomain.shopping.com%2Fcommon%2Fdart_wrapper_001.html%3F%2Fadj%2Fgtr.forsale%2Fdublin%253Bl2%253D%253Bkw%253D%253Bpos%253D%253Bsz%253D728x90%252C468x60%253Btile%253D1%253Bposting_cat%253D1958%253Bpage_type%253Dposting_static%253B&ndr=http%3A%2F%2Fsearch.aol.com%2Faol%2Fsearch%3Fs_it%3Dcomsearch40%26q%3Dcamping%2Bocean%2Bcity&pt=s"
		append htmlFileName $Config(pixelType) "_" $Config(id) "_IMG_" $Config(dataPixelFormat) "_NdlNdr.html" 
	} elseif { $NdlNdr == "N" } {
		set NdlNdr ""
		append htmlFileName $Config(pixelType) "_" $Config(id) "_IMG_" $Config(dataPixelFormat) ".html" 
	} else {
		puts "ERROR: Wrong value of NdlNdr"
		exit
	}

	if { $Config(isHttps) == "Y" } {
		set prot "https"
		set port "9410"
	} elseif { $Config(isHttps) == "N" } {
		set prot "http"
		set port "8080"
	} else {
		puts "ERROR: Wrong value of isHttps"
		exit
	}
	
	puts "htmlFileName= $htmlFileName"
	set fHtml [open $htmlFileName w]
	set content "\
		<HTML><HEAD>\r\n\
		<TITLE>$Config(ruleSelection)</TITLE>\r\n\
		</HEAD><BODY>\r\n\
		<h1>$Config(pixelType) (id=$Config(id)) </h1>\r\n\
		<!-- NOTE: t=i image pixel, t=s script!!! -->\r\n\
		<b> Data Pixel with tag img</b>\r\n\
		<img width=\"1\" height=\"1\" src=\"$prot://p.raasnet.com:$port/partners/universal/in?pid=$Config(id)&t=$Config(dataPixelFormat)$NdlNdr\"/>\r\n\
		</BODY></HTML>\r\n"
	puts $fHtml $content
	close $fHtml
}

proc createDataPixelTemplate_DOCUMENT_WRITE { NdlNdr type } {
global Config htmlFolder

	if { $Config(isHttps) == "Y" } {
		set prot "https"
		set port "9410"
	} elseif { $Config(isHttps) == "N" } {
		set prot "http"
		set port "8080"
	} else {
		puts "ERROR: Wrong value of isHttps"
	}
	
	if { $type == "img" } {
		set contentTypeBegin "img width=\"0\" height=\"0\""
		set contentTypeEnd ""
	} elseif { $type == "script" } {
		if { $Config(dataPixelFormat) == "F" } {
			set contentTypeBegin "ifr' + 'ame"
			set contentTypeEnd "</ifr' + 'ame>"
			set type "frame"
		} else {
			set contentTypeBegin "scr' + 'ipt"
			set contentTypeEnd "</scr' + 'ipt>"
		}
	} else {
		puts "ERROR: Wrong value of document.write type img|script"
	}

	set htmlFileName $htmlFolder
	if { $NdlNdr == "Y" } {
		set NdlNdr "&ndl=http%3A%2F%2Fdomain.shopping.com%2Fcommon%2Fdart_wrapper_001.html%3F%2Fadj%2Fgtr.forsale%2Fdublin%253Bl2%253D%253Bkw%253D%253Bpos%253D%253Bsz%253D728x90%252C468x60%253Btile%253D1%253Bposting_cat%253D1958%253Bpage_type%253Dposting_static%253B&ndr=http%3A%2F%2Fsearch.aol.com%2Faol%2Fsearch%3Fs_it%3Dcomsearch40%26q%3Dcamping%2Bocean%2Bcity&pt=s"
		append htmlFileName $Config(pixelType) "_" $Config(id) "_DOCUMENT_WRITE_" $Config(dataPixelFormat) "_" $type "_NdlNdr.html" 
	} elseif { $NdlNdr == "N" } {
		set NdlNdr ""
		append htmlFileName $Config(pixelType) "_" $Config(id) "_DOCUMENT_WRITE_" $Config(dataPixelFormat) "_" $type ".html" 
	} else {
		puts "ERROR: Wrong value of NdlNdr"
		exit
	}
	
	puts "htmlFileName= $htmlFileName"
	set fHtml [open $htmlFileName w]
	set content "\
		<HTML><HEAD>\r\n\
		<TITLE>$Config(ruleSelection)</TITLE>\r\n\
		</HEAD><BODY>\r\n\
		<h1>$Config(pixelType) pixel with tag img (id=$Config(id)) </h1>\r\n\
		<!--\r\n\
		NOTE: WE DO NOT NEED NDR/NDL section because image tag means that script is disabled on the publisher site.\r\n\
		In this case, we're trying to get the publisher URL from the header\r\n\
		referee and record it to NDL, and send it to IC\r\n\
		-->\r\n\
		<script>document.write('<$contentTypeBegin src=\"$prot://p.raasnet.com:$port/partners/universal/in?pid=$Config(id)&t=$Config(dataPixelFormat)$NdlNdr\">$contentTypeEnd');</script>\r\n\
		</BODY></HTML>\r\n"
	puts $fHtml $content
	close $fHtml
}

proc createDataPixelHTML {} {
global Config
	#arg1 = NdlNdr = [ Y N ]
	#arg2 = type = [ "img" | "script" { if ("I" | "S") then "script"; if ("F") then "frame"} ]
	
	createDataPixelTemlate_SCRIPT

	createDataPixelTemlate_IMG "Y"
	createDataPixelTemlate_IMG "N"
	
	createDataPixelTemplate_DOCUMENT_WRITE "Y" "img"
	createDataPixelTemplate_DOCUMENT_WRITE "N" "img"
	
	createDataPixelTemplate_DOCUMENT_WRITE "Y" "script"
	createDataPixelTemplate_DOCUMENT_WRITE "N" "script"
	
}

proc createCampaignPixelHTML {} {
	createCampaignPixelTemplate_SCRIPT
	createCampaignPixelTemplate_DOCUMENT_WRITE
	createCampaignPixelTemplate_IMG
}


####################################################################################################

global Config templateFolder htmlFolder
init

	set i 0
	foreach file [glob [file join $templateFolder "*.txt"]] {
		incr i
		puts "i= $i; file= [file tail $file]"
		parseTemplateFile [file tail $file]
	}

