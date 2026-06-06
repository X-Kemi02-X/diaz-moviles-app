package com.diazmoviles.app.di

import com.diazmoviles.app.data.repository.AuthRepositoryImpl
import com.diazmoviles.app.data.repository.CartRepositoryImpl
import com.diazmoviles.app.data.repository.ClienteRepositoryImpl
import com.diazmoviles.app.data.repository.ProductoRepositoryImpl
import com.diazmoviles.app.data.repository.ProveedorRepositoryImpl
import com.diazmoviles.app.data.repository.VentaRepositoryImpl
import com.diazmoviles.app.domain.repository.AuthRepository
import com.diazmoviles.app.domain.repository.CartRepository
import com.diazmoviles.app.domain.repository.ClienteRepository
import com.diazmoviles.app.domain.repository.ProductoRepository
import com.diazmoviles.app.domain.repository.ProveedorRepository
import com.diazmoviles.app.domain.repository.VentaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductoRepository(impl: ProductoRepositoryImpl): ProductoRepository

    @Binds
    @Singleton
    abstract fun bindClienteRepository(impl: ClienteRepositoryImpl): ClienteRepository

    @Binds
    @Singleton
    abstract fun bindVentaRepository(impl: VentaRepositoryImpl): VentaRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindProveedorRepository(impl: ProveedorRepositoryImpl): ProveedorRepository
}
