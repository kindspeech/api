package org.kindspeech.api

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

private object TableText : Table("Text") {
    val id = integer("id")
    val text = varchar("text", 255)
    val attribution = varchar("attribution", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}

data class Text(val text: String, val attribution: String?)

class Database {

    init {
        initDatabase()
    }

    fun randomText(maxLength: Int? = null): Text {
        return transaction {
            // TODO Optimize for selecting a single random row.
            TableText
                // It's significantly faster to ORDER BY RAND() on an indexed column.
                .joinQuery(on = { it[TableText.id] eq TableText.id }) {
                    TableText.slice(TableText.id).selectAll().orderBy(Random()).limit(1)
                }
                .slice(TableText.text, TableText.attribution)
                .run {
                    if (maxLength != null) {
                        select { LengthFunction(TableText.text) lessEq maxLength }
                    } else {
                        selectAll()
                    }
                }
                .single()
                .let {
                    Text(it[TableText.text], it[TableText.attribution])
                }
        }
    }

    companion object {

        private val PROJECT_ID = requireEnv("KS_PROJECT_ID")
        private val CLOUD_SQL_CONNECTION_NAME = requireEnv("KS_CLOUD_SQL_CONNECTION_NAME")
        private val DB_USER = requireEnv("KS_DB_USER")
        private val DB_PASSWORD_SECRET = requireEnv("KS_DB_PASSWORD_SECRET")
        private val DB_PASSWORD_SECRET_VERSION = requireEnv("KS_DB_PASSWORD_SECRET_VERSION")
        private val DB_NAME = requireEnv("KS_DB_NAME")

        private fun requireEnv(name: String): String {
            return checkNotNull(System.getenv(name)) {
                "Environment variable $name must be set"
            }
        }

        private fun initDatabase() {
            val config = HikariConfig()

            config.jdbcUrl = "jdbc:mysql:///$DB_NAME"
            config.username = DB_USER
            config.password = getPassword()

            config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory")
            config.addDataSourceProperty("cloudSqlInstance", CLOUD_SQL_CONNECTION_NAME)

            val pool: DataSource = HikariDataSource(config)

            Database.connect(pool)
        }

        private fun getPassword(): String {
            val client = SecretManagerServiceClient.create()
            val secretVersionName = SecretVersionName.of(PROJECT_ID, DB_PASSWORD_SECRET, DB_PASSWORD_SECRET_VERSION)
            val response = client.accessSecretVersion(secretVersionName)
            return response.payload.data.toStringUtf8()
        }
    }
}

class LengthFunction<T: ExpressionWithColumnType<String>>(private val exp: T) : Function<Int>(IntegerColumnType()) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        append("CHAR_LENGTH(", exp, ')')
    }
}
