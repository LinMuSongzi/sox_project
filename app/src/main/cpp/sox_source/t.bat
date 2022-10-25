@echo off 
set format=%1 
shift 
set opts=%1 %2 %3 %4 %5 %6 %7 %8 %9 
 
cls 
echo Format: %format%   Options: %opts% 
echo on 
.\sox monkey.wav %opts% %tmp%\monkey.%format% %effect% 
.\sox %opts% %tmp%\monkey.%format% %tmp%\monkey1.wav %effect% 
@echo off 
echo. 
set format=
set opts=
pause 
