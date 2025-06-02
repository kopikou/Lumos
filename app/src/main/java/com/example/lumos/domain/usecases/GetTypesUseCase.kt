package com.example.lumos.domain.usecases

import com.example.lumos.data.repository.TypeRepositoryImpl
import com.example.lumos.domain.entities.Type

class GetTypesUseCase(private val typeRepository: TypeRepositoryImpl) {
    suspend operator fun invoke(): List<Type> {
        return typeRepository.getTypes()
    }
}