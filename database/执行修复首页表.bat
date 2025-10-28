@echo off
chcp 65001 >nul
echo ========================================
echo 修复首页数据表结构并导入测试数据
echo ========================================
echo.
echo 正在执行步骤 1/2：修复表结构...
mysql -hlocalhost -uroot -p123456 bb < 快速修复-首页数据表.sql

if %ERRORLEVEL% NEQ 0 (
    echo 错误：表结构修复失败
    pause
    exit /b 1
)

echo.
echo 正在执行步骤 2/2：导入测试数据...
mysql -hlocalhost -uroot -p123456 bb < insert_public_homepage_data.sql

if %ERRORLEVEL% NEQ 0 (
    echo 错误：数据导入失败
    pause
    exit /b 1
)

echo.
echo ========================================
echo ✓ 修复完成！
echo ========================================
echo.
echo 下一步：
echo 1. 重启后端 Spring Boot 应用
echo 2. 刷新前端页面
echo 3. 访问首页查看数据
echo.
pause

