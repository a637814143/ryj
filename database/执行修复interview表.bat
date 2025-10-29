@echo off
chcp 65001 >nul
echo ================================================
echo 修复 interview 表结构
echo ================================================
echo.

REM 尝试查找MySQL
set "MYSQL_CMD=mysql"
set "MYSQL_PATHS=C:\Program Files\MySQL\MySQL Server 8.0\bin;C:\Program Files\MySQL\MySQL Server 8.4\bin;C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin"

REM 检查mysql是否在PATH中
where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo MySQL未在PATH中找到，尝试常见安装路径...
    for %%p in (%MYSQL_PATHS%) do (
        if exist "%%p\mysql.exe" (
            set "MYSQL_CMD=%%p\mysql.exe"
            echo ✓ 找到 MySQL：%%p\mysql.exe
            goto :found
        )
    )
    echo.
    echo ❌ 未找到 MySQL！
    echo.
    echo 请手动在 Navicat 中执行 fix_interview_table.sql
    echo.
    pause
    exit /b 1
)

:found
echo.
echo 正在修复 interview 表...
echo.

REM 执行SQL文件（使用引号包裹MYSQL_CMD以处理空格）
"%MYSQL_CMD%" -u root -p bb < fix_interview_table.sql

if %errorlevel% equ 0 (
    echo.
    echo ================================================
    echo ✓ interview 表修复成功！
    echo ================================================
) else (
    echo.
    echo ================================================
    echo ❌ 修复失败！
    echo ================================================
    echo.
    echo 请手动在 Navicat 中执行 fix_interview_table.sql
)

echo.
pause


