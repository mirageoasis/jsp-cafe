import random
import string

# 이메일 생성
def generate_email(user_id):
    return f"{user_id}@example.com"

# 사용자 생성
users = []
for i in range(1, 10000+1):
    user_id = f"user{i}"
    password = "password"
    nickname = f"nickname_{i}"
    email = generate_email(user_id)
    users.append((user_id, password, nickname, email))

# 게시글 생성
articles = []
for i in range(500000):
    title = f"Title {i}"
    content = f"Content of article {i}"
    author = random.choice(users)[0]  # user_id
    articles.append((title, content, author))

# 댓글 생성
replies = []
for i, (title, content, author) in enumerate(articles):
    num_replies = random.randint(1, 10)
    for j in range(num_replies):
        reply_content = f"Reply content {i}-{j}"
        reply_author = random.choice(users)[0]  # user_id
        replies.append((i + 1, reply_content, reply_author))  # article_id, content, author

# SQL INSERT 문 생성 (배치로 처리)
BATCH_SIZE = 100000

# 사용자 INSERT
user_inserts = []
for i in range(0, len(users), BATCH_SIZE):
    batch = users[i:i + BATCH_SIZE]
    values = ", ".join([f"('{user[0]}', '{user[1]}', '{user[2]}', '{user[3]}')" for user in batch])
    user_inserts.append(f"INSERT INTO users (user_id, password, nickname, email) VALUES {values};")

# 게시글 INSERT
article_inserts = []
for i in range(1, len(articles)+1, BATCH_SIZE):
    batch = articles[i:i + BATCH_SIZE]
    values = ", ".join([f"('{article[0]}', '{article[1]}', '{article[2]}')" for article in batch])
    article_inserts.append(f"INSERT INTO articles (title, content, author) VALUES {values};")

# 댓글 INSERT
reply_inserts = []
for i in range(0, len(replies), BATCH_SIZE):
    batch = replies[i:i + BATCH_SIZE]
    values = ", ".join([f"({reply[0]}, '{reply[1]}', '{reply[2]}')" for reply in batch])
    reply_inserts.append(f"INSERT INTO replies (article_id, content, author) VALUES {values};")

# 파일로 저장
with open("insert_users.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(user_inserts))

with open("insert_articles.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(article_inserts))

with open("insert_replies.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(reply_inserts))

print("SQL 파일 생성 완료")
