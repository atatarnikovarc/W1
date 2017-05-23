@echo off

C:
chdir C:\nebuad\ACTIVE_PROJECTS\PerlScripts\jmxcommandline

java -jar cmdline-jmxclient-0.10.3.zip nebuad:n3buad 10.50.150.152:9999 NebuAd.identitymanager:name=IdentityManager reset
