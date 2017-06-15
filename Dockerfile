###
#
#  WebAnno + EXMARaLDA Dockerfile
#
###

FROM tomcat:8-jre8

MAINTAINER Steffen Remus

RUN set -ex \
      && DEBIAN_FRONTEND=noninteractive \
      && apt-get update \
      && apt-get install -y --no-install-recommends ca-certificates locales tomcat8-user authbind

RUN set -ex \
      && sed -i -e 's/# en_US.UTF-8 UTF-8/en_US.UTF-8 UTF-8/' /etc/locale.gen \
      && dpkg-reconfigure --frontend=noninteractive locales \
      && update-locale LANG=en_US.UTF-8

ENV LANG en_US.UTF-8

WORKDIR /opt

RUN tomcat8-instance-create -p 18080 -c 18005 webanno

RUN chown -R www-data /opt/webanno

RUN curl -kL https://webanno.github.io/webanno/releases/3.1.0/docs/admin-guide/scripts/webanno | sed -E 's/-Xmx[0-9]+[MmGgKk]?/-Xmx6g/g' > /etc/init.d/webanno \
      && chmod +x /etc/init.d/webanno

# RUN curl -kLo /opt/webanno/webapps/webanno-exm.war https://github.com/remstef/webanno-exmaralda/releases/download/v-3.3.0-alpha.1/webanno-webapp-exm-3.3.0-SNAPSHOT.war
COPY ${PWD}/webanno-webapp-exm/target/webanno-webapp-exm-3.3.0-SNAPSHOT.war /opt/webanno/webapps/webanno-exm.war

VOLUME /srv/webanno

#RUN curl -o /settings.properties http://....
COPY settings.properties /settings.properties

RUN chown -R www-data /srv/webanno

EXPOSE 18080

CMD echo "starting..." \
      && ([ -e /srv/webanno/settings.properties ] || cp /settings.properties /srv/webanno/) \
      && sleep 1 \
      && chown -R www-data /srv/webanno \
      && sleep 1 \
      && /etc/init.d/webanno start \
      && tail -f /opt/webanno/logs/catalina.out
