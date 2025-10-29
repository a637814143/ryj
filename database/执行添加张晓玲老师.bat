@echo off
chcp 65001 >nul
echo ================================================
echo 添加张晓玲老师账号到数据库
echo ================================================
echo.

REM 定义常见的MySQL安装路径
set "MYSQL_PATHS=C:\Program Files\MySQL\MySQL Server 8.0\bin;C:\Program Files\MySQL\MySQL Server 5.7\bin;C:\xampp\mysql\bin;C:\wamp64\bin\mysql\mysql8.0.27\bin"

REM 尝试查找mysql.exe
set "MYSQL_CMD="
for %%p in ("%MYSQL_PATHS:;=" "%") do (
    if exist %%p\mysql.exe (
        set MYSQL_CMD="%%p\mysql.exe"
        goto :found
    )
)

REM 如果没找到，尝试使用PATH中的mysql
where mysql >nul 2>&1
if %ERRORLEVEL% == 0 (
    set "MYSQL_CMD=mysql"
    goto :found
)

:notfound
echo ❌ 错误：未找到 MySQL 命令行工具！
echo.
echo 请手动执行以下步骤：
echo 1. 打开 Navicat 或其他 MySQL 客户端
echo 2. 连接到 bb 数据库
echo 3. 打开并执行文件：add_teacher_zhangxiaoling.sql
echo.
echo 或者手动运行命令：
echo mysql -u root -p bb ^< add_teacher_zhangxiaoling.sql
echo.
pause
exit /b 1

:found
echo ✓ 找到 MySQL：%MYSQL_CMD%
echo.
echo 正在执行 SQL 文件...
echo.

REM 执行SQL文件
%MYSQL_CMD% -u root -p bb < add_teacher_zhangxiaoling.sql

if %ERRORLEVEL% == 0 (
    echo.
    echo ================================================
    echo ✓ 张晓玲老师账号添加成功！
    echo ================================================
    echo.
    echo 【登录信息】
    echo 用户名: teacher_zhang
    echo 密码: 000000
    echo 姓名: 张晓玲
    echo 角色: TEACHER
    echo.
) else (
    echo.
    echo ================================================
    echo ❌ 执行失败！
    echo ================================================
    echo.
    echo 请检查：
    echo 1. MySQL 服务是否正在运行
    echo 2. 数据库连接密码是否正确
    echo 3. bb 数据库是否存在
    echo.
    echo 或者使用 Navicat 手动执行 add_teacher_zhangxiaoling.sql
    echo.
)

pause

