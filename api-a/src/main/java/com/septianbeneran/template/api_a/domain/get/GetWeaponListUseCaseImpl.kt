package com.septianbeneran.template.api_a.domain.get

import com.septianbeneran.template.api_a.data.repository.WeaponRepository
import com.septianbeneran.template.core.remote.entity.ApiResult
import com.septianbeneran.template.core_entity.a.Weapon
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetWeaponListUseCaseImpl @Inject constructor(
    private val repo: WeaponRepository
) : GetWeaponListUseCase {
    override fun invoke(
        limit: Int?,
        name: String?,
        page: Int?
    ): Flow<ApiResult<List<Weapon>>> = repo.getWeaponList(limit, name, page)
}