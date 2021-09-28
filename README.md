# tel-tools
## Add repository in CentOs 7
cp id_rsa.pub ~/.ssh/id_rsa.pub

eval \`ssh-agent\`

ssh-add

ssh-add -l

mkdir tel-tools

cd tel-tools

git init

git remote add origin git@github.com:IPSI-AU/tel-tools.git

git pull origin master

git config --global user.name "Your Name"

git config --global user.email "Your email"

