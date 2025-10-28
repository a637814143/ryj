@echo off
chcp 65001 >nul
echo ========================================
echo 修复employer表结构
echo ========================================
echo.

set MYSQL_USER=root
set MYSQL_PASSWORD=123456
set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set DATABASE_NAME=bb

echo 正在连接数据库...
echo 数据库: %DATABASE_NAME%
echo 主机: %MYSQL_HOST%:%MYSQL_PORT%
echo.

mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% < fix_employer_table.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ✓ employer表修复成功！
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ✗ 修复失败，请检查错误信息
    echo ========================================
)

echo.
pause

