FROM debian:buster
WORKDIR /root/
ADD . /root
RUN ./setup.sh
CMD ["/start.sh"]
EXPOSE 80 3306 8983 8161
