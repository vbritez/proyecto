package com.tigo.cs.api.facade;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tigo.cs.commons.util.MaxIdleLRUCacheV6;

public class CacheAPI<T> extends AbstractAPI<String> implements Serializable {

    private static final long serialVersionUID = 3549509239669209211L;

    protected CacheAPI() {
        super(String.class);
    }

    private MaxIdleLRUCacheV6<String, T> cache;

    private long sleepTime = 5000;
    private Thread showCache;
    private Thread putCache;
    private Thread refresh;

    private CacheParameterData cacheParameterData;

    class CacheParameterData implements Comparable {
        private Long sleepTime;
        private Integer maxEntries;
        private Long minAge;

        public CacheParameterData(Long sleepTime, Integer maxEntries,
                Long minAge) {
            super();
            this.sleepTime = sleepTime;
            this.maxEntries = maxEntries;
            this.minAge = minAge;
        }

        public Long getSleepTime() {
            return sleepTime;
        }

        public void setSleepTime(Long sleepTime) {
            this.sleepTime = sleepTime;
        }

        public Integer getMaxEntries() {
            return maxEntries;
        }

        public void setMaxEntries(Integer maxEntries) {
            this.maxEntries = maxEntries;
        }

        public Long getMinAge() {
            return minAge;
        }

        public void setMinAge(Long minAge) {
            this.minAge = minAge;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result
                + ((maxEntries == null) ? 0 : maxEntries.hashCode());
            result = prime * result
                + ((minAge == null) ? 0 : minAge.hashCode());
            result = prime * result
                + ((sleepTime == null) ? 0 : sleepTime.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CacheParameterData other = (CacheParameterData) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (maxEntries == null) {
                if (other.maxEntries != null)
                    return false;
            } else if (!maxEntries.equals(other.maxEntries))
                return false;
            if (minAge == null) {
                if (other.minAge != null)
                    return false;
            } else if (!minAge.equals(other.minAge))
                return false;
            if (sleepTime == null) {
                if (other.sleepTime != null)
                    return false;
            } else if (!sleepTime.equals(other.sleepTime))
                return false;
            return true;
        }

        @Override
        public int compareTo(Object o) {
            return this.equals(o) ? 0 : 1;
        }

        private CacheAPI getOuterType() {
            return CacheAPI.this;
        }

        @Override
        public String toString() {
            return "CacheParameterData [sleepTime=" + sleepTime
                + ", maxEntries=" + maxEntries + ", minAge=" + minAge + "]";
        }

    }

    public void refresh() {

        try {

            getFacadeContainer().getNotifier().debug(getClass(), null,
                    "CACHE - Se verifica la regeneracion del cache ");

            CacheParameterData tempCacheParameterData = new CacheParameterData(Long.valueOf(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "cache.thread.millisencods.sleep")), Integer.valueOf(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "cache.max.entries")), Long.valueOf(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "cache.min.age.milliseconds")));

            getFacadeContainer().getNotifier().debug(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "CACHE - Se obtuvieron los valores para generear el cache. {0}",
                            tempCacheParameterData.toString()));

            if (putCache == null) {
                putCache = new Thread() {

                    @Override
                    public void run() {
                        while (true) {
                            try {
                                if (cache != null) {
                                    cache.put("1", null);
                                    cache.remove("1");
                                    Thread.sleep(10000);
                                }
                            } catch (InterruptedException e) {
                                getFacadeContainer().getNotifier().error(
                                        getClass(), null,
                                        "ERROR: " + e.getMessage(), e);
                            }
                        }
                    }
                };

                putCache.setDaemon(true);
                putCache.start();
            }

            if (refresh == null) {
                refresh = new Thread() {

                    @Override
                    public void run() {
                        while (true) {
                            try {
                                refresh();
                                Thread.sleep(10 * 60 * 1000);

                            } catch (InterruptedException e) {
                                getFacadeContainer().getNotifier().error(
                                        getClass(), null,
                                        "ERROR: " + e.getMessage(), e);
                            }
                        }
                    }
                };

                refresh.setDaemon(true);
                refresh.start();
            }

            if (!tempCacheParameterData.equals(cacheParameterData)) {

                getFacadeContainer().getNotifier().info(
                        getClass(),
                        null,
                        MessageFormat.format(
                                "CACHE - Cambios en los parametros del cache. Old:{0}. New:{1}",
                                cacheParameterData != null ? cacheParameterData.toString() : "",
                                tempCacheParameterData.toString()));

                getFacadeContainer().getNotifier().info(getClass(), null,
                        "CACHE - Se creara el nuevo cache con los parametros nuevos.");

                if (cache != null) {
                    cache.setMaxCacheEntries(tempCacheParameterData.getMaxEntries());
                    cache.setMinAge(tempCacheParameterData.getMinAge());
                    getFacadeContainer().getNotifier().info(getClass(), null,
                            "CACHE - Cache modificado");
                } else {

                    cache = new MaxIdleLRUCacheV6<String, T>(tempCacheParameterData.getMinAge(), tempCacheParameterData.getMaxEntries(), new MaxIdleLRUCacheV6.DeadElementCallback<String, T>() {

                        @Override
                        public void notify(String key, T element, int timesOfUses) {
                            getFacadeContainer().getNotifier().debug(
                                    getClass(),
                                    null,
                                    "Cache. Eliminando: " + key + " --> "
                                        + element);

                        }
                    });
                    getFacadeContainer().getNotifier().info(getClass(), null,
                            "CACHE - Cache creado");
                }

                sleepTime = tempCacheParameterData.getSleepTime();

                if (showCache == null) {

                    showCache = new Thread() {

                        private final Date fechaCreacionHilo = new Date();
                        private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    if (cache != null) {
                                        String title = "CACHE - Cache - ";
                                        Long age = cache.relaxSize();
                                        Thread.sleep(sleepTime);
                                        getFacadeContainer().getNotifier().debug(
                                                getClass(), null,
                                                title + "Informacion");
                                        getFacadeContainer().getNotifier().info(
                                                getClass(),
                                                null,
                                                title + "Cantidad "
                                                    + cache.size());
                                        getFacadeContainer().getNotifier().debug(
                                                getClass(),
                                                null,
                                                title
                                                    + MessageFormat.format(
                                                            "Edad en segundos elemento mas antiguo:{0,number,#}",
                                                            age / 1000));
                                        getFacadeContainer().getNotifier().debug(
                                                getClass(),
                                                null,
                                                title
                                                    + "Fecha creacion "
                                                    + format.format(fechaCreacionHilo));
                                        getFacadeContainer().getNotifier().debug(
                                                getClass(),
                                                null,
                                                title + "Fecha creacion Cache "
                                                    + cache.getFechaCreacion());
                                        getFacadeContainer().getNotifier().debug(
                                                getClass(), null,
                                                title + "Contenido " + cache);
                                    }
                                } catch (InterruptedException e) {
                                    getFacadeContainer().getNotifier().error(
                                            getClass(), null,
                                            "ERROR: " + e.getMessage(), e);
                                }
                            }
                        }
                    };

                    getFacadeContainer().getNotifier().debug(getClass(), null,
                            "CACHE - Nuevo hilo de verificacion de cache subscriber creado");

                    showCache.setDaemon(true);
                    showCache.start();

                    getFacadeContainer().getNotifier().debug(getClass(), null,
                            "CACHE - Nuevo hilo de verificacion de cache subscriber corriendo");
                }
                cacheParameterData = tempCacheParameterData;
                getFacadeContainer().getNotifier().debug(getClass(), null,
                        "CACHE - Se reemplazan los parametros del cache subscriber con los nuevos");

            }

        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    "ERROR: " + e.getMessage(), e);
        }
    }

    public MaxIdleLRUCacheV6<String, T> getCache() {
        return cache;
    }

    public void setCache(MaxIdleLRUCacheV6<String, T> cache) {
        this.cache = cache;
    }

}
