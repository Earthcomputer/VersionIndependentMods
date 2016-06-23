@echo off

FOR %%I IN (vimapi-*.jar) DO (
	IF DEFINED vimFile (
		GOTO error_tooManyVIMs
	)
	SET vimFile=%%I
)

IF NOT DEFINED vimFile (
	GOTO error_noVIM
)

java -jar "%vimFile%" "%*"

GOTO end

:error_tooManyVIMs
ECHO Error: more than one VIM candidate found
GOTO end

:error_noVIM
ECHO Error: no VIM found
GOTO end

:end
PAUSE
@echo on
