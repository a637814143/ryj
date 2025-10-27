@echo off
chcp 65001 >nul
echo ========================================
echo 导入职位招聘测试数据
echo ========================================
echo.

REM 请根据您的MySQL安装路径修改下面的路径
REM 常见路径: C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
REM 或者: C:\xampp\mysql\bin\mysql.exe

set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

echo 即将执行SQL导入...
echo 请在弹出的提示中输入MySQL root密码
echo.

%MYSQL_PATH% -u root -p < insert_job_test_data.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo 数据导入成功！
    echo ========================================
    echo.
    echo 已成功导入：
    echo - 8 家企业信息
    echo - 15 个职位信息
    echo - 多条职位要求
    echo.
) else (
    echo.
    echo ========================================
    echo 数据导入失败，请检查：
    echo ========================================
    echo 1. MySQL是否已启动
    echo 2. MySQL路径是否正确
    echo 3. root密码是否正确
    echo 4. 数据库bb是否存在
    echo.
)

pause

