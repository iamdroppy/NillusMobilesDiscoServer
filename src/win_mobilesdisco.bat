@echo off


REM Start the MobilesDisco class with 'java'
REM arg0 = blunk.properties: environment variables for Blunk environment
REM arg1 = mobilesdisco.properties: basic configuration variables for Mobiles Disco project
java net.nillus.mobilesdisco.MobilesDisco blunk.properties mobilesdisco.properties

pause