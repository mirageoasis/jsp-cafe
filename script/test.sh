#!/bin/bash

# 데이터베이스 사용자 정보
python3 test.py

DB_USER="root"
DB_PASSWORD="root"  # 고정된 비밀번호
DB_NAME="test"

# SQL 파일 실행 함수
execute_sql_file() {
  local sql_file=$1
  echo "Executing $sql_file..."
  mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME < $sql_file
  if [ $? -eq 0 ]; then
    echo "$sql_file executed successfully."
  else
    echo "Error executing $sql_file."
    exit 1
  fi
}

# SQL 파일 실행
execute_sql_file "insert_users.sql"
execute_sql_file "insert_articles.sql"
execute_sql_file "insert_replies.sql"

echo "All SQL scripts executed successfully."
